package com.tenderowls.moorka.mkml.components

import com.tenderowls.moorka.core._
import com.tenderowls.moorka.mkml.components.Repeat.ItemRenderer
import com.tenderowls.moorka.mkml.dom._
import com.tenderowls.moorka.mkml.engine._

import scala.collection.mutable

/**
 * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
 */
object Repeat {

  type ItemRenderer[A] = (A) => ElementBase

  def apply[A](dataProvider: CollectionView[A], itemRenderer: (A) => ElementBase) = {
    new Repeat[A](Var(dataProvider), itemRenderer)
  }

  def apply[A](dataProvider: Bindable[CollectionView[A]], itemRenderer: ItemRenderer[A]) = {
    new Repeat[A](dataProvider, itemRenderer)
  }
}

class Repeat[A](val dataProvider: Bindable[CollectionView[A]],
                val itemRenderer: ItemRenderer[A])

  extends ElementBase with MKML {

  case class Child(data:A, dom:ElementBase)

  private val displayState = mutable.HashMap[Child, Boolean]()

  private val observers = mutable.HashMap[Child, Event[_]]()

  private var _viewFilter = (_: A) => true

  private var _rxExtractor: (A) => Bindable[_] = null

  private var _dataProvider = dataProvider()

  private var children: CollectionView[Child] = null

  val ref = Ref("div")

  dataProvider observe { _ =>
    updateDataProvider()
  }

  private def updateDisplayStateOfChild(child: Child) = {
    if (_viewFilter(child.data)) {
      if (!displayState(child)) {
        child.dom.ref.classRemove("hidden")
        displayState(child) = true
      }
    }
    else {
      if (displayState(child)) {
        child.dom.ref.classAdd("hidden")
        displayState(child) = false
      }
    }
  }

  private def createObserver(child: Child) = {
    if (_rxExtractor != null) {
      observers(child) = _rxExtractor(child.data) subscribe { _ =>
        updateDisplayStateOfChild(child)
      }
    }
  }

  private def killObserver(child: Child) = {
    if (_rxExtractor != null) {
      observers(child).kill()
    }
  }

  private def updateDataProvider() = {

    kill()
    _dataProvider = dataProvider()

    children = _dataProvider.map { x =>
      val child = Child(x, itemRenderer(x))
      displayState(child) = true
      updateDisplayStateOfChild(child)
      createObserver(child)
      child
    }

    ref.appendChildren(children.asSeq.map(_.dom.ref))

    children.foreach(_.dom.parent = this)

    children.added subscribe { x =>
      x.dom.parent = this
      ref.appendChild(x.dom.ref)
    }

    children.inserted subscribe { x =>
      x.idx + 1 match {
        case idx if idx < children.length =>
          x.e.dom.parent = this
          ref.insertChild(x.e.dom.ref, children(idx).dom.ref)
        case _ =>
          x.e.dom.parent = this
          ref.appendChild(x.e.dom.ref)
      }
    }

    children.removed subscribe { x =>
      x.e.dom.parent = null
      ref.removeChild(x.e.dom.ref)
      killObserver(x.e)
      x.e.dom.kill()
    }

    children.updated subscribe { x =>
      val oldChild = children(x.idx)
      oldChild.dom.parent = null
      oldChild.dom.kill()
      x.e.dom.parent = this
      ref.replaceChild(x.e.dom.ref, oldChild.dom.ref)
    }
  }

  /**
   * Sets elements invisible when they are not satisfy filter
   */
  def viewFilter(f: (A) => Boolean): Repeat[A] = {
    _viewFilter = f
    children.foreach(updateDisplayStateOfChild)
    this
  }

  def makeDataObservable(f: (A) => Bindable[_]): Repeat[A] = {
    _rxExtractor = f
    children.foreach(createObserver)
    this
  }

  override def kill(): Unit = {
    super.kill()
    if (children != null) children.kill()
    observers.values.foreach(_.kill())
  }

  SyntheticEventProcessor.registerElement(this)
}

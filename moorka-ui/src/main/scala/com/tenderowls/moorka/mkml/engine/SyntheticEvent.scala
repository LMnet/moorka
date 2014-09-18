package com.tenderowls.moorka.mkml.engine

import com.tenderowls.moorka.mkml.dom.ElementBase
import org.scalajs.dom

/**
 * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
 */
sealed trait SyntheticEvent {

  private[mkml] var _bubbles:Boolean = false

  private[mkml] var _cancelable:Boolean = false

  private[mkml] var _currentTarget:ElementBase = null

  private[mkml] var _target:ElementBase = null

  private[mkml] var _timestamp:Long = 0l

  private[mkml] var _defaultPrevented:Boolean = false

  private[mkml] var _eventPhase: EventPhase = Idle

  private[mkml] var _propagationStopped:Boolean = false

  def bubbles:Boolean = _bubbles

  def cancelable:Boolean = _cancelable

  def currentTarget:ElementBase = _currentTarget

  def target:ElementBase = _target

  def timestamp:Long = _timestamp

  def defaultPrevented:Boolean = _defaultPrevented

  def eventPhase: EventPhase = _eventPhase

  def preventDefault() = {
    _defaultPrevented = true
  }

  def stopPropagation() = {
    _propagationStopped = true
  }
}

final class FormEvent extends SyntheticEvent {

}

sealed class MouseEvent extends SyntheticEvent {

  private[mkml] var _nativeEvent: dom.MouseEvent = null

  private[mkml] var _altKey: Boolean = false

  def altKey: Boolean = _altKey

  private[mkml] var _button: Int = 0

  def button: Int = _button

  private[mkml] var _buttons: Int  = 0

  def buttons: Int = _buttons

  private[mkml] var _clientX: Int  = 0

  def clientX: Int = _clientX

  private[mkml] var _clientY: Int  = 0

  def clientY: Int = _clientY

  private[mkml] var _ctrlKey: Boolean = false

  def ctrlKey: Boolean = _ctrlKey

  def getModifierState(key: String): Boolean = {
    _nativeEvent.getModifierState(key)
  }

  private[mkml] var _metaKey: Boolean = false

  def metaKey: Boolean = _metaKey

  private[mkml] var _pageX: Int = 0

  def pageX: Int = _pageX

  private[mkml] var _pageY: Int = 0

  def pageY: Int = _pageY

  private[mkml] var _relatedTarget: ElementBase = null

  def relatedTarget: ElementBase = _relatedTarget

  private[mkml] var _screenX: Int  = 0

  def screenX: Int = _screenX

  private[mkml] var _screenY: Int = 0

  def screenY: Int = _screenY

  private[mkml] var _shiftKey: Boolean = false

  def shiftKey: Boolean = _shiftKey
}

sealed trait EventPhase

case object Idle extends EventPhase

case object Capturing extends EventPhase

case object AtTarget extends EventPhase

case object Bubbling extends EventPhase


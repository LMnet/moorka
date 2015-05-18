package vaska

import scala.concurrent.Future

/**
 * Link to entity on page side. By default, all links 
 * will be removed by GC cause its have no references in a page. 
 * PageLink give you ability to `save()` it. When you don't need
 * the link no more you can `free()` it.
 * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
 */
trait JSLink {

  val jsAccess: JSAccess

  val id: String
  
  /**
   * Tell page to save reference to the link to avoid
   * garbage collection 
   */
  def save(): Future[Unit] = {
    jsAccess.request("save", this, id)
  }

  /**
   * Tell page you don't need the link no more.
   */
  def free(): Future[Unit] = {
    jsAccess.request("free", this, id)
  }
}
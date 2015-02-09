package controllers

import controllers.ru.arcww.mail.client.IMAPClient
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Application extends Controller {

  def index = Action.async(IMAPClient.listMessages.map(l => Ok(views.html.index(l))))

}
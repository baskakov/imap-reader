package controllers.ru.arcww.mail.client

import javax.mail._
import javax.mail.internet._
import javax.mail.search._
import java.util.Properties
import ru.arcww.mail.client.MailReader

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object IMAPClient {

  def listMessages = {
    val p = Promise[List[(String,String)]]()
    val props = System.getProperties()
    props.setProperty("mail.store.protocol", "imaps")
    Future {
      val session = Session.getDefaultInstance(props, null)
      val store = session.getStore("imaps")
      println("connecting")
      try {
        // use imap.gmail.com for gmail
        store.connect("imap.mail.ru", "morris_filip@list.ru", "12345678@")
        val inbox = store.getFolder("Inbox")
        inbox.open(Folder.READ_WRITE)
        println("Opened")
        // limit this to 20 message during testing

        val messages = inbox.getMessages().toList.map(message => message.getSubject -> {


          val body = message match {
            case m: MimeMessage => {
              val contentObject = m.getContent()
              contentObject match {
                case multi: Multipart => {
                  val count = multi.getCount
                  (0 until count).map(i => {
                    val part = multi.getBodyPart(i)
                    part.getContent.toString
                  }).mkString("\r\n")
                }
                case s: String => s
                case u => "Unknown body type " + u.toString
              }
            }
            case m => "Unknown message type " + m.toString
          }
          body
        })

/*        val limit = 20
        var count = 0
        for (message <- messages) {
          count = count + 1
          if (count > limit) System.exit(0)
          println(message.getSubject())
        }*/
        p.complete(Success(messages))
        println("finished")
        inbox.close(true)
      } catch {
        case e: Exception => e.printStackTrace()
          p.complete(Failure(e))
      } finally {
        if(!p.isCompleted) p.complete(Failure(new IllegalStateException("Other IMAP future exception")))
        store.close()
      }
    }
    p.future
  }
}

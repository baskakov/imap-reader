package ru.arcww.fbl

class ARFParser {
  val RegTo = """To: <([^>]*)>""".r
  val RegFrom = """From: <([^>]*)>""".r
  val RegRemovalRecipient = """Removal-Recipient: ([a-zA-Z0-9@.]*)""".r
}

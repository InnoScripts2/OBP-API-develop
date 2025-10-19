package code.api.util

import code.api.RequestHeader._
import net.liftweb.http.provider.HTTPParam

object AuthorisationUtil {
  def getAuthorisationHeaders(requestHeaders: List[HTTPParam]): List[String] = {
    requestHeaders.map(_.name).filter {
      case `Consent-Id`| `Consent-ID` | `Consent-JWT` => true
      case _ => false
    }
  }
  

}

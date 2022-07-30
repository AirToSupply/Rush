package tech.odes

import java.util.Properties

import tech.odes.rush.common.exception.RushException

package object rush {

  private object RushInfo {
    val (
      version: String
    ) = {
      val resourceStream = Thread.currentThread().getContextClassLoader
        .getResourceAsStream("rush-version.properties")
      if (resourceStream == null) {
        throw new RushException("Could not find rush-version.properties")
      }

      try {
        val unknownProp = "<unknown>"
        val props = new Properties()
        props.load(resourceStream)

        (
          props.getProperty("version", unknownProp)
        )

      } catch {
        case e: Exception =>
          throw new RushException("Error loading properties from rush-version.properties", e)
      } finally {
        if (resourceStream != null) {
          try {
            resourceStream.close()
          } catch {
            case e: Exception =>
              throw new RushException("Error closing ides build info resource stream", e)
          }
        }
      }
    }
  }

  val __RUSH_VERSION = RushInfo.version

  val __RUSH_BANNER =
    """Welcome to

            _____                   _____                   _____                   _____
           /\    \                 /\    \                 /\    \                 /\    \
          /::\    \               /::\____\               /::\    \               /::\____\
         /::::\    \             /:::/    /              /::::\    \             /:::/    /
        /::::::\    \           /:::/    /              /::::::\    \           /:::/    /
       /:::/\:::\    \         /:::/    /              /:::/\:::\    \         /:::/    /
      /:::/__\:::\    \       /:::/    /              /:::/__\:::\    \       /:::/____/
     /::::\   \:::\    \     /:::/    /               \:::\   \:::\    \     /::::\    \
    /::::::\   \:::\    \   /:::/    /      _____   ___\:::\   \:::\    \   /::::::\    \   _____
   /:::/\:::\   \:::\____\ /:::/____/      /\    \ /\   \:::\   \:::\    \ /:::/\:::\    \ /\    \
  /:::/  \:::\   \:::|    |:::|    /      /::\____/::\   \:::\   \:::\____/:::/  \:::\    /::\____\
  \::/   |::::\  /:::|____|:::|____\     /:::/    \:::\   \:::\   \::/    \::/    \:::\  /:::/    /
   \/____|:::::\/:::/    / \:::\    \   /:::/    / \:::\   \:::\   \/____/ \/____/ \:::\/:::/    /
         |:::::::::/    /   \:::\    \ /:::/    /   \:::\   \:::\    \              \::::::/    /
         |::|\::::/    /     \:::\    /:::/    /     \:::\   \:::\____\              \::::/    /
         |::| \::/____/       \:::\__/:::/    /       \:::\  /:::/    /              /:::/    /
         |::|  ~|              \::::::::/    /         \:::\/:::/    /              /:::/    /
         |::|   |               \::::::/    /           \::::::/    /              /:::/    /
         \::|   |                \::::/    /             \::::/    /              /:::/    /
          \:|   |                 \::/____/               \::/    /               \::/    /  (version: %s)
           \|___|                  ~~                      \/____/                 \/____/


    """.format(__RUSH_VERSION)
}

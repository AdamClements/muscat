#Muscat

<img src="http://upload.wikimedia.org/wikipedia/commons/f/fb/Muscat_rouge_de_madere.jpg" align="right" width="30%"/>

NOTE, THIS IS STILL IN PRODUCTION AND NOT QUITE READY FOR USE YET.

Muscat is an MQTT client library for clojure and clojurescript.

MQTT is a publish/subscribe protocol, therefore muscat has two
functions in its public API: `publish` and `subscribe`.

It comes with a couple of different strategies for coercing payloads,
from plain UTF-8 string to EDN data, providing sensible quick-start
defaults as well as allowing you to extend it to arbitrary use
cases. For example if you wanted, you could use the excellent nippy
serialisation library (recommended for clojure, unavailable for
clojurescript and it would be too opinionated to include it in muscat
by default), or if you prefer, it would be trivial to add in fressian
support.

## Usage

First install an MQTT broker. Mosquitto if only using clojure, for
clojurescript Hive is recommended though usage is restricted to 25
connections for the free version, however does work with websockets by
default, requiring only a minor tweak to the configuration file
(enable the websockets options). Mosquitto client can happily connect
to Hive server and vice versa. Mosquitto has the handy `mosquitto_pub`
and `mosquitto_sub` for listening to and sending messages via plain
string on the command line, which is useful for debugging.

### Basic

Publishing will try and re-use connections where it can, however
subscriptions will each start a new connection to the server. In
future this may change so that everything attempts to reuse the same
connection where possible, however that will be transparent to the
API.



### Overriding payload coercion

### Retry strategy

## License

Copyright © 2014 Adam Clements

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

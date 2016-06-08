from stompest.config import StompConfig
from stompest.sync import Stomp
from config import Config


# TODO to make this one asynish with stompest.async? think about it
class AMQPublisher:

    ip = Config.get("bus_ip")
    port = Config.get("bus_port_stomp")
    user = Config.get("bus_login")
    pw = Config.get("bus_pass")

    def __init__(self):
        stompConfig = StompConfig("tcp://" + self.ip + ":" + self.port,
                                  login=self.user, passcode=self.pw)
        self.client = Stomp(stompConfig)
        self.client.connect()

    def publish(self, topic, message):
        self.client.send("/topic/" + topic, message)

import stomp
from config import Config


class AMQPublisher:

    ip = Config.get("bus_ip")
    port = Config.get("bus_port_stomp")
    user = Config.get("bus_login")
    pw = Config.get("bus_pass")

    def __init__(self):
        self.c = stomp.Connection([(self.ip, int(self.port))])
        self.c.start()
        self.c.connect(self.user, self.pw, wait=True)

    def publish(self, topic, message):
        self.c.send(body=message, destination="/topic/" + topic)

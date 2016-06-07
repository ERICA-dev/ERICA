import stomp


class AMQPublisher:

    def __init__(self):
        self.c = stomp.Connection([('127.0.0.1', 61613)])
        self.c.start()
        self.c.connect('admin', 'admin', wait=True)

    def publish(self, topic, message):
        self.c.send(body=message, destination="/topic/" + topic)

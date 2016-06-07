from config import Config
import stomp
# from threading import Thread
import time


class AMQSubscriber:

    ip = Config.get("bus_ip")
    port = Config.get("bus_port_stomp")
    user = Config.get("bus_login")
    pw = Config.get("bus_pass")

    # TODO revise use of thread, it should be useful with multiple
    # subscriptions right?
    def subscribe(self, topic, on_msg):
        # thread = Thread(target=self._unthreaded_subscribe,
        #                 args=(topic, on_msg))
        # thread.daemon = True
        # thread.start()
        self._unthreaded_subscribe(topic, on_msg)

    def _unthreaded_subscribe(self, topic, on_msg):
        self.on_msg = on_msg
        c = stomp.Connection([(self.ip, int(self.port))])
        listener = self._Listener()
        listener.set_on_msg(on_msg)
        c.set_listener('', listener)
        c.start()
        c.connect(self.user, self.pw, wait=True)
        c.subscribe(destination="/topic/" + topic, id=1, ack='auto')
        while True:
            time.sleep(1)

    class _Listener(stomp.ConnectionListener):

        on_msg = None

        def set_on_msg(self, on_msg):
            self.on_msg = on_msg

        def on_message(self, headers, message):
            self.on_msg(message)

import stomp
import time


class PrintingListener(stomp.ConnectionListener):

    def on_error(self, headers, message):
        print "errorerrorerror"

    def on_message(self, headers, message):
        print "meddelande!"
        print message

c = stomp.Connection([('127.0.0.1', 61613)])
c.set_listener('', PrintingListener())
c.start()
c.connect('admin', 'admin', wait=True)

c.subscribe(destination='/hej', id=1, ack='auto')

c.send(body='hejjeee', destination='/hej')

c.send(body="hejjeee", destination='/hej')

time.sleep(2)

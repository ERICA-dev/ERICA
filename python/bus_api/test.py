from AMQSubscriber import AMQSubscriber
from AMQPublisher import AMQPublisher
import unittest
import time


class TestStringFromPublisherToSubscriber(unittest.TestCase):

    returnedstr = ""

    # test names must begin with test
    def test_msg(self):
        sentstr = "haaerafsojwfh"
        amqPublisher = AMQPublisher()
        amqSubscriber = AMQSubscriber()

        def on_msg(mess):
            self.returnedstr = mess
        amqSubscriber.subscribe("testTopik", on_msg)
        amqPublisher.publish("testTopik", sentstr)
        time.sleep(0.01)
        self.assertEqual(self.returnedstr, sentstr)

if __name__ == '__main__':
    unittest.main()

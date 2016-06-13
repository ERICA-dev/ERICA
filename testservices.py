from bus_api.AMQSubscriber import AMQSubscriber
from bus_api.AMQPublisher import AMQPublisher
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
        time.sleep(0.1)
        self.assertEqual(self.returnedstr, sentstr)


class TestJSONInJSONOut(unittest.TestCase):

    returnedjson = ""

    def test_json(self):
        sentjson = "{'banan': 'albatross'}"
        amqPublisher = AMQPublisher()
        amqSubscriber = AMQSubscriber()

        def on_msg(mess):
            self.returnedjson = mess
        amqSubscriber.subscribe("testTopik", on_msg)
        amqPublisher.publish("testTopik", sentjson)
        time.sleep(0.1)
        self.assertEqual(self.returnedjson, sentjson)


class TestQueueIncrement(unittest.TestCase):

    returnedstr = ""

    def test_queue_increment(self):
        # erica_event = '{"Title": "Arrival", "Value": "albatross", "Category": "banan", "Start": 1337, "End": 1337, "SubjectId": 1337}'
        erica_event = '{"Type": "Arrival",\
                        "Title": "mayo je",\
                        "Value": "albatross",\
                        "Category": "mu",\
                        "Start": "today",\
                        "End": "alsoToday",\
                        "SubjectId": "whoKnows"}'
        supposedreturn = '{"Feature":"Queue","Change":"+"}'

        amqPublisher = AMQPublisher()
        amqSubscriber = AMQSubscriber()

        def on_msg(mess):
            self.returnedstr = mess
        amqSubscriber.subscribe("PredictionFeatures", on_msg)
        # time.sleep(0.1)
        amqPublisher.publish("EricaEvents", erica_event)
        time.sleep(0.1)
        self.assertEqual(self.returnedstr, supposedreturn)


if __name__ == '__main__':
    unittest.main()

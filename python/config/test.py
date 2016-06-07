import unittest
import types
import Config


class TestConfigGetType(unittest.TestCase):

    # test names must begin with test
    def test_get(self):
        bus_ip = Config.get("bus_ip")
        self.assertTrue(type(bus_ip) in types.StringTypes)

if __name__ == '__main__':
    unittest.main()

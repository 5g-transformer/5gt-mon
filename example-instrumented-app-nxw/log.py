import logging
from logging import DEBUG, LoggerAdapter

import sys
import re


APPLICATION_NAME = 'baseserver'


class BracesAdapter(LoggerAdapter):
    BRACES_PATTERN = re.compile(r'{([a-zA-Z0-9]+)?(:[ +]?\d*(\.\d+)?[df])?}')

    @staticmethod
    def substitute(m):
        output = '%'
        if m.group(1) is not None:
            output += '(' + m.group(1) + ')'
        if m.group(2) is not None:
            output += m.group(2).lstrip(':')
        else:
            output += 's'
        return output

    def process(self, msg, kwargs):
        new_msg = self.BRACES_PATTERN.sub(self.substitute, msg)
        return new_msg, kwargs


class ShortFormatter(logging.Formatter):
    """The logging.Formatter subclass"""

    level_name_table = {
        'CRITICAL': 'CRT',
        'ERROR': 'ERR',
        'WARNING': 'WRN',
        'INFO': 'INF',
        'DEBUG': 'DBG'
    }

    def __init__(self):
        super(ShortFormatter, self).__init__(
            fmt='%(asctime)s | %(levelname)3.3s | '
                '%(name)11.11s | %(message)s',
            datefmt='%H:%M:%S')

    def format(self, record):
        record.name = record.name.split('.')[-1]
        record.levelname = self.level_name_table[record.levelname]
        return super(ShortFormatter, self).format(record)


handler = logging.StreamHandler(sys.stdout)
handler.setLevel(logging.DEBUG)
formatter = ShortFormatter()
handler.setFormatter(formatter)
logging.basicConfig(handlers=[handler], level=logging.DEBUG)
logging.getLogger('').setLevel(logging.ERROR)

_logger = logging.getLogger(APPLICATION_NAME)

root_log = BracesAdapter(_logger, extra={})

root_log.setLevel(DEBUG)


def get_logger(name, extra=None):
    # type: (str) -> LoggerAdapter
    if extra is None:
        extra = {}
    return BracesAdapter(_logger.getChild(name), extra=extra)

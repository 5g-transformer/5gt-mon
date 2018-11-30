from json import dumps
from time import time

from prometheus_client import Counter, Gauge, Summary
from random import random, randint
from tornado.gen import sleep
from tornado.web import RequestHandler

from log import get_logger


logger = get_logger(__name__)


class IndexHandler(RequestHandler):

    s = Summary('app_index_processing_time_seconds', 'Time spent processing the index')
    s2 = Summary('app_index_total_time_seconds', 'Time spent loading the index')
    c = Counter('app_index_total', 'Total number of index pages loaded')
    g = Gauge('app_index_serving', 'Number of index pages that are being loaded right now')

    @s.time()
    async def get(self):
        start = time()
        self.c.inc()
        with self.g.track_inprogress():
            self.write(
                """
                <!DOCTYPE html>
                <html>
                <body>
                
                <h1>Some index page</h1>
                
                <p>Lorem ipsum dolor sit amet, consectetur adipisci elit, sed eiusmod tempor incidunt ut labore et dolore 
                magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, 
                nisi ut aliquid ex ea commodi consequatur.</p>
                <p>Quis aute iure reprehenderit in voluptate velit esse cillum 
                dolore eu fugiat nulla pariatur.</p>
                <p>Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia 
                deserunt mollit anim id est laborum</p>
                </body>
                </html>
                """
            )
            # buuut, let's say there's a lot of data to be loaded first
            loading_time = random() * 2
            await sleep(loading_time)
        total = max(time() - start, 0)
        self.s2.observe(total)


class PostHandler(RequestHandler):

    s = Summary('app_post_processing_time_seconds', 'Time spent serving post requests')
    cf = Counter('app_post_failed', 'Total number of failed post requests')
    cs = Counter('app_post_success', 'Total number of successful post requests')
    g = Gauge('app_post_serving', 'Number of post requests that are being served right now')

    @g.track_inprogress()
    async def post(self):
        with self.s.time():
            # Does an operation, that may fail
            response = {
                    'response': 42,
                    'successful': randint(0, 1) == 1
                }

            if response['successful']:
                self.cs.inc()
            else:
                self.cf.inc()

            self.write(dumps(response))

            # and of course, it takes time
            loading_time = random() * 2
            await sleep(loading_time)

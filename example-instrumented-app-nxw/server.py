from argparse import ArgumentParser
from json import loads
from random import randint, random

from prometheus_client import Histogram, Counter, start_http_server
from tornado.gen import sleep
from tornado.httpclient import AsyncHTTPClient
from tornado.httpclient import HTTPResponse
from tornado.ioloop import IOLoop
from tornado.web import Application

from handlers import IndexHandler, PostHandler
from log import get_logger, DEBUG


logger = get_logger(__name__)
logger.setLevel(DEBUG)


ADDRESS = 'http://localhost'


h = Histogram('app_client_request_latency_seconds', 'Latency of a call', ['method'])
c = Counter('app_client_requests_sent', 'Number of requests sent', ['status'])

hp = h.labels('post')
hg = h.labels('get')


def make_app():
    """
    """
    return Application([
        (r'/', IndexHandler),
        (r'/post', PostHandler)
    ])


async def post(client: AsyncHTTPClient, port: int):
    with hp.time():
        response = await client.fetch('{}:{}/post'.format(ADDRESS, port), method='POST', body=b'{}')  # type: HTTPResponse
        if 200 <= response.code < 300:
            if loads(response.body.decode())['successful']:
                logger.debug('POST success')
                c.labels('success').inc()
            else:
                logger.debug('POST failure')
                c.labels('failure').inc()
        else:
            logger.error('Request failed.', response.error)


async def get(client: AsyncHTTPClient, port: int):
    with hg.time():
        response = await client.fetch('{}:{}'.format(ADDRESS, port), method='GET')
        if 200 <= response.code < 300:
            logger.debug('One success')
        else:
            logger.error('Request failed.', response.error)


async def request_periodically(port: int, loop: IOLoop):
    client = AsyncHTTPClient()
    while True:
        gets = randint(0, 10)
        posts = randint(0, 10)
        for _ in range(gets):
            loop.add_callback(get, client, port)
        for _ in range(posts):
            loop.add_callback(post, client, port)
        await sleep(random() * 5.5)


def main(port, mon_port):
    app = make_app()
    app.listen(port)
    try:
        logger.info("Listening on port number {}", port)
        start_http_server(mon_port)
        # cb = partial(request_periodically, port, IOLoop.current())
        IOLoop.current().spawn_callback(request_periodically, port, IOLoop.current())
        IOLoop.current().start()
    except KeyboardInterrupt or SystemExit:
        logger.info("Shutting down.")
    finally:
        IOLoop.current().stop()


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('-P', '--port', metavar='PORT',
                        help='port to listen to',
                        type=int, default=8080)
    parser.add_argument('-R', '--prom-port', metavar='PROM-PORT',
                        help='port for prometheus mon',
                        type=int, default=9090)
    args = parser.parse_args()
    main(args.port, args.prom_port)

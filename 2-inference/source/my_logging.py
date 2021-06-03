import logging
import datetime
import sys


def beijing(sec, what):
    beijing_time = datetime.datetime.now() + datetime.timedelta(hours=8)
    return beijing_time.timetuple()

logger_initialized = {}
logging.Formatter.converter = beijing
def get_logger(name='predictor', log_level=logging.INFO):
    logger = logging.getLogger(name)
    if name in logger_initialized:
        return logger
    stream_handler = logging.StreamHandler(stream=sys.stdout)
    formatter = logging.Formatter('[%(asctime)s] %(name)s %(levelname)s: %(message)s',datefmt="%Y/%m/%d %H:%M:%S")
    stream_handler.setFormatter(formatter)
    logger.addHandler(stream_handler)
    logger.setLevel(log_level)
    logger_initialized[name] = True
    return logger
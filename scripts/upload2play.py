import argparse
import sys
from apiclient import sample_tools
from oauth2client import client

"""
Uploads an apk to the alpha track.
https://github.com/googlesamples/android-play-publisher-api/tree/master/v3/python
"""

argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('package_name', help='The package name. Example: com.android.sample')
argparser.add_argument('apk_file',
                       nargs='?',
                       default='test.apk',
                       help='The path to the APK file to upload.')


def main(argv):
    # Authenticate and construct service.
    service, flags = sample_tools.init(
        argv,
        'androidpublisher',
        'v3',
        __doc__,
        __file__,
        parents=[argparser],
        scope="https://www.googleapis.com/auth/androidpublisher"
    )

    # Process flags and read their values.
    package_name = flags.package_name
    apk_file = flags.apk_file


if __name__ == '__main__':
    main(sys.argv)

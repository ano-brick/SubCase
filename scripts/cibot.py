import asyncio
import os
import sys
from telethon import TelegramClient

TG_API_ID = int(os.environ.get("TG_API_ID"))
TG_API_HASH = os.environ.get("TG_API_HASH")
TG_BOT_TOKEN = os.environ.get("TG_BOT_TOKEN")
TG_CHAT_ID = int(os.environ.get("TG_CHAT_ID"))

COMMIT_URL = os.environ.get("COMMIT_URL")
COMMIT_MESSAGE = os.environ.get("COMMIT_MESSAGE")
RUN_URL = os.environ.get("RUN_URL")
MSG_TEMPLATE = """
#SubCase

New push to github!

```
{commit_message}
```

Commit: {commit_url}
Action: {run_url}
""".strip()


def get_caption():
    msg = MSG_TEMPLATE.format(
        commit_message=COMMIT_MESSAGE.strip(),
        commit_url=COMMIT_URL,
        run_url=RUN_URL
    )
    if len(msg) > 1024:
        return COMMIT_URL
    return msg


async def main():
    print("[+] Uploading to telegram")
    files = sys.argv[1:]
    print("[+] Files:", files)
    if len(files) <= 0:
        print("[-] No files to upload")
        exit(1)
    print("[+] Logging in Telegram with bot")
    script_dir = os.path.dirname(os.path.abspath(sys.argv[0]))
    session_dir = os.path.join(script_dir, "cibot.session")
    async with await TelegramClient(session=session_dir, api_id=TG_API_ID, api_hash=TG_API_HASH).start(
            bot_token=TG_BOT_TOKEN) as bot:
        caption = [""] * len(files)
        caption[-1] = get_caption()
        print("[+] Caption: ")
        print("---")
        print(caption)
        print("---")
        print("[+] Sending")
        await bot.send_file(entity=TG_CHAT_ID, file=files, caption=caption, parse_mode="markdown")
        print("[+] Done!")


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as e:
        print(f"[-] An error occurred: {e}")
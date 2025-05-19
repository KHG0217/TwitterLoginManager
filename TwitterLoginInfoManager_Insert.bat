@echo off
setlocal enabledelayedexpansion

if not exist "twitter_insert" (
    mkdir "twitter_insert"
)

for /f %%a in ('wmic os get localdatetime ^| find "."') do set datetime=%%a
set "year=!datetime:~0,4!"
set "month=!datetime:~4,2!"
set "day=!datetime:~6,2!"
set "hour=!datetime:~8,2!"
set "minute=!datetime:~10,2!"
set "second=!datetime:~12,2!"

set "filename=twitter_insert_log_!year!!month!!day!_!hour!!minute!!second!.txt"

java -cp target\twitter-login-info-manager-3.0.jar com.tapacross.sns.crawler.twitter.TwitterLoginInfoManager 1 > ./twitter_insert/"%filename%"
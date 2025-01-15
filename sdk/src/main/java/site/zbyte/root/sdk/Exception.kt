package site.zbyte.root.sdk

class NoSupportedAbiException : Exception()

class BadProcessExitException(code:Int):Exception("exit code: $code")

class TimeoutException: Exception()

class StartProcessException(throwable: Throwable):Exception(throwable)
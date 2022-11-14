package com.example.kavyasynechrontask.data.cloud.exceptions

import java.io.IOException

class InvalidConfigDataException(val origin: Exception) : IOException("Invalid config data")
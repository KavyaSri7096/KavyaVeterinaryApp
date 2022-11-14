package com.example.kavyasynechrontaskapp.common.fetchimage.exception

import java.io.IOException

class UnsuccessfulImageRequest(url: String) : IOException("Unsuccessful image request to $url")
package com.skampe.utils.handlers;

import okhttp3.RequestBody;

public abstract class OkHttpRequestBodyHandler {

	public abstract RequestBody build();
}

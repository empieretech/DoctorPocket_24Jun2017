package com.docpoc.doctor.webServices;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MyCustomMultiPartEntity extends MultipartEntity {

	private final ProgressListener listener;

	private CountingOutputStream outputStream_;
	private OutputStream lastOutputStream_;

	public MyCustomMultiPartEntity(final ProgressListener listener) {
		super();
		this.listener = listener;
	}

	public MyCustomMultiPartEntity(final HttpMultipartMode mode,
                                   final ProgressListener listener) {
		super(mode);
		this.listener = listener;
	}

	public MyCustomMultiPartEntity(HttpMultipartMode mode,
                                   final String boundary, final Charset charset,
                                   final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}

	// @Override
	// public boolean isChunked() {
	// return true;
	// }

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		// super.writeTo(new CountingOutputStream(outstream, this.listener));

		// If we have yet to create the CountingOutputStream, or the
		// OutputStream being passed in is different from the OutputStream used
		// to create the current CountingOutputStream
		if ((lastOutputStream_ == null) || (lastOutputStream_ != outstream)) {
			lastOutputStream_ = outstream;
			outputStream_ = new CountingOutputStream(outstream, this.listener);
		}

		super.writeTo(outputStream_);

	}

	public static interface ProgressListener {
		void transferred(long num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
}
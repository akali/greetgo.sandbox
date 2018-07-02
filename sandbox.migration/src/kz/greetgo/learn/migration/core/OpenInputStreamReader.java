package kz.greetgo.learn.migration.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class OpenInputStreamReader extends InputStreamReader {
  public OpenInputStreamReader(InputStream inputStream) {
    super(inputStream);
  }

  public OpenInputStreamReader(InputStream inputStream, String s) throws UnsupportedEncodingException {
    super(inputStream, s);
  }

  public OpenInputStreamReader(InputStream inputStream, Charset charset) {
    super(inputStream, charset);
  }

  public OpenInputStreamReader(InputStream inputStream, CharsetDecoder charsetDecoder) {
    super(inputStream, charsetDecoder);
  }

  private boolean autoClose = true;

  @Override
  public void close() throws IOException {
    if (autoClose)
      super.close();
  }

  public void setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
  }
}

// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  struct Error
//  {
//      ErrorCode code;
//      string msg<100>;
//  };

//  ===========================================================================
public class Error  {
  public Error () {}
  private ErrorCode code;
  public ErrorCode getCode() {
    return this.code;
  }
  public void setCode(ErrorCode value) {
    this.code = value;
  }
  private String msg;
  public String getMsg() {
    return this.msg;
  }
  public void setMsg(String value) {
    this.msg = value;
  }
  public static void encode(XdrDataOutputStream stream, Error encodedError) throws IOException{
    ErrorCode.encode(stream, encodedError.code);
    stream.writeString(encodedError.msg);
  }
  public static Error decode(XdrDataInputStream stream) throws IOException {
    Error decodedError = new Error();
    decodedError.code = ErrorCode.decode(stream);
    decodedError.msg = stream.readString();
    return decodedError;
  }
}

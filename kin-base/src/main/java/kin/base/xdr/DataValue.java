// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  typedef opaque DataValue<64>;

//  ===========================================================================
public class DataValue  {
  private byte[] DataValue;
  public byte[] getDataValue() {
    return this.DataValue;
  }
  public void setDataValue(byte[] value) {
    this.DataValue = value;
  }
  public static void encode(XdrDataOutputStream stream, DataValue  encodedDataValue) throws IOException {
  int DataValuesize = encodedDataValue.DataValue.length;
  stream.writeInt(DataValuesize);
  stream.write(encodedDataValue.getDataValue(), 0, DataValuesize);
  }
  public static DataValue decode(XdrDataInputStream stream) throws IOException {
    DataValue decodedDataValue = new DataValue();
  int DataValuesize = stream.readInt();
  decodedDataValue.DataValue = new byte[DataValuesize];
  stream.read(decodedDataValue.DataValue, 0, DataValuesize);
    return decodedDataValue;
  }
}

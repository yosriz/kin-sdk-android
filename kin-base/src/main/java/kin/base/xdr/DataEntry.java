// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  struct DataEntry
//  {
//      AccountID accountID; // account this data belongs to
//      string64 dataName;
//      DataValue dataValue;
//  
//      // reserved for future use
//      union switch (int v)
//      {
//      case 0:
//          void;
//      }
//      ext;
//  };

//  ===========================================================================
public class DataEntry  {
  public DataEntry () {}
  private AccountID accountID;
  public AccountID getAccountID() {
    return this.accountID;
  }
  public void setAccountID(AccountID value) {
    this.accountID = value;
  }
  private String64 dataName;
  public String64 getDataName() {
    return this.dataName;
  }
  public void setDataName(String64 value) {
    this.dataName = value;
  }
  private DataValue dataValue;
  public DataValue getDataValue() {
    return this.dataValue;
  }
  public void setDataValue(DataValue value) {
    this.dataValue = value;
  }
  private DataEntryExt ext;
  public DataEntryExt getExt() {
    return this.ext;
  }
  public void setExt(DataEntryExt value) {
    this.ext = value;
  }
  public static void encode(XdrDataOutputStream stream, DataEntry encodedDataEntry) throws IOException{
    AccountID.encode(stream, encodedDataEntry.accountID);
    String64.encode(stream, encodedDataEntry.dataName);
    DataValue.encode(stream, encodedDataEntry.dataValue);
    DataEntryExt.encode(stream, encodedDataEntry.ext);
  }
  public static DataEntry decode(XdrDataInputStream stream) throws IOException {
    DataEntry decodedDataEntry = new DataEntry();
    decodedDataEntry.accountID = AccountID.decode(stream);
    decodedDataEntry.dataName = String64.decode(stream);
    decodedDataEntry.dataValue = DataValue.decode(stream);
    decodedDataEntry.ext = DataEntryExt.decode(stream);
    return decodedDataEntry;
  }

  public static class DataEntryExt {
    public DataEntryExt () {}
    Integer v;
    public Integer getDiscriminant() {
      return this.v;
    }
    public void setDiscriminant(Integer value) {
      this.v = value;
    }
    public static void encode(XdrDataOutputStream stream, DataEntryExt encodedDataEntryExt) throws IOException {
    stream.writeInt(encodedDataEntryExt.getDiscriminant().intValue());
    switch (encodedDataEntryExt.getDiscriminant()) {
    case 0:
    break;
    }
    }
    public static DataEntryExt decode(XdrDataInputStream stream) throws IOException {
    DataEntryExt decodedDataEntryExt = new DataEntryExt();
    Integer discriminant = stream.readInt();
    decodedDataEntryExt.setDiscriminant(discriminant);
    switch (decodedDataEntryExt.getDiscriminant()) {
    case 0:
    break;
    }
      return decodedDataEntryExt;
    }

  }
}

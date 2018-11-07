// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  enum MemoType
//  {
//      MEMO_NONE = 0,
//      MEMO_TEXT = 1,
//      MEMO_ID = 2,
//      MEMO_HASH = 3,
//      MEMO_RETURN = 4
//  };

//  ===========================================================================
public enum MemoType  {
  MEMO_NONE(0),
  MEMO_TEXT(1),
  MEMO_ID(2),
  MEMO_HASH(3),
  MEMO_RETURN(4),
  ;
  private int mValue;

  MemoType(int value) {
      mValue = value;
  }

  public int getValue() {
      return mValue;
  }

  static MemoType decode(XdrDataInputStream stream) throws IOException {
    int value = stream.readInt();
    switch (value) {
      case 0: return MEMO_NONE;
      case 1: return MEMO_TEXT;
      case 2: return MEMO_ID;
      case 3: return MEMO_HASH;
      case 4: return MEMO_RETURN;
      default:
        throw new RuntimeException("Unknown enum value: " + value);
    }
  }

  static void encode(XdrDataOutputStream stream, MemoType value) throws IOException {
    stream.writeInt(value.getValue());
  }
}

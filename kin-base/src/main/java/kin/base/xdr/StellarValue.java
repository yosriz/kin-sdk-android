// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  struct StellarValue
//  {
//      Hash txSetHash;   // transaction set to apply to previous ledger
//      uint64 closeTime; // network close time
//  
//      // upgrades to apply to the previous ledger (usually empty)
//      // this is a vector of encoded 'LedgerUpgrade' so that nodes can drop
//      // unknown steps during consensus if needed.
//      // see notes below on 'LedgerUpgrade' for more detail
//      // max size is dictated by number of upgrade types (+ room for future)
//      UpgradeType upgrades<6>;
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
public class StellarValue  {
  public StellarValue () {}
  private Hash txSetHash;
  public Hash getTxSetHash() {
    return this.txSetHash;
  }
  public void setTxSetHash(Hash value) {
    this.txSetHash = value;
  }
  private Uint64 closeTime;
  public Uint64 getCloseTime() {
    return this.closeTime;
  }
  public void setCloseTime(Uint64 value) {
    this.closeTime = value;
  }
  private UpgradeType[] upgrades;
  public UpgradeType[] getUpgrades() {
    return this.upgrades;
  }
  public void setUpgrades(UpgradeType[] value) {
    this.upgrades = value;
  }
  private StellarValueExt ext;
  public StellarValueExt getExt() {
    return this.ext;
  }
  public void setExt(StellarValueExt value) {
    this.ext = value;
  }
  public static void encode(XdrDataOutputStream stream, StellarValue encodedStellarValue) throws IOException{
    Hash.encode(stream, encodedStellarValue.txSetHash);
    Uint64.encode(stream, encodedStellarValue.closeTime);
    int upgradessize = encodedStellarValue.getUpgrades().length;
    stream.writeInt(upgradessize);
    for (int i = 0; i < upgradessize; i++) {
      UpgradeType.encode(stream, encodedStellarValue.upgrades[i]);
    }
    StellarValueExt.encode(stream, encodedStellarValue.ext);
  }
  public static StellarValue decode(XdrDataInputStream stream) throws IOException {
    StellarValue decodedStellarValue = new StellarValue();
    decodedStellarValue.txSetHash = Hash.decode(stream);
    decodedStellarValue.closeTime = Uint64.decode(stream);
    int upgradessize = stream.readInt();
    decodedStellarValue.upgrades = new UpgradeType[upgradessize];
    for (int i = 0; i < upgradessize; i++) {
      decodedStellarValue.upgrades[i] = UpgradeType.decode(stream);
    }
    decodedStellarValue.ext = StellarValueExt.decode(stream);
    return decodedStellarValue;
  }

  public static class StellarValueExt {
    public StellarValueExt () {}
    Integer v;
    public Integer getDiscriminant() {
      return this.v;
    }
    public void setDiscriminant(Integer value) {
      this.v = value;
    }
    public static void encode(XdrDataOutputStream stream, StellarValueExt encodedStellarValueExt) throws IOException {
    stream.writeInt(encodedStellarValueExt.getDiscriminant().intValue());
    switch (encodedStellarValueExt.getDiscriminant()) {
    case 0:
    break;
    }
    }
    public static StellarValueExt decode(XdrDataInputStream stream) throws IOException {
    StellarValueExt decodedStellarValueExt = new StellarValueExt();
    Integer discriminant = stream.readInt();
    decodedStellarValueExt.setDiscriminant(discriminant);
    switch (decodedStellarValueExt.getDiscriminant()) {
    case 0:
    break;
    }
      return decodedStellarValueExt;
    }

  }
}

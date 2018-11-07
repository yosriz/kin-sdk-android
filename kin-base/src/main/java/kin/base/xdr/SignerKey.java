// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten

package kin.base.xdr;


import java.io.IOException;

// === xdr source ============================================================

//  union SignerKey switch (SignerKeyType type)
//  {
//  case SIGNER_KEY_TYPE_ED25519:
//      uint256 ed25519;
//  case SIGNER_KEY_TYPE_PRE_AUTH_TX:
//      /* Hash of Transaction structure */
//      uint256 preAuthTx;
//  case SIGNER_KEY_TYPE_HASH_X:
//      /* Hash of random 256 bit preimage X */
//      uint256 hashX;
//  };

//  ===========================================================================
public class SignerKey  {
  public SignerKey () {}
  SignerKeyType type;
  public SignerKeyType getDiscriminant() {
    return this.type;
  }
  public void setDiscriminant(SignerKeyType value) {
    this.type = value;
  }
  private Uint256 ed25519;
  public Uint256 getEd25519() {
    return this.ed25519;
  }
  public void setEd25519(Uint256 value) {
    this.ed25519 = value;
  }
  private Uint256 preAuthTx;
  public Uint256 getPreAuthTx() {
    return this.preAuthTx;
  }
  public void setPreAuthTx(Uint256 value) {
    this.preAuthTx = value;
  }
  private Uint256 hashX;
  public Uint256 getHashX() {
    return this.hashX;
  }
  public void setHashX(Uint256 value) {
    this.hashX = value;
  }
  public static void encode(XdrDataOutputStream stream, SignerKey encodedSignerKey) throws IOException {
  stream.writeInt(encodedSignerKey.getDiscriminant().getValue());
  switch (encodedSignerKey.getDiscriminant()) {
  case SIGNER_KEY_TYPE_ED25519:
  Uint256.encode(stream, encodedSignerKey.ed25519);
  break;
  case SIGNER_KEY_TYPE_PRE_AUTH_TX:
  Uint256.encode(stream, encodedSignerKey.preAuthTx);
  break;
  case SIGNER_KEY_TYPE_HASH_X:
  Uint256.encode(stream, encodedSignerKey.hashX);
  break;
  }
  }
  public static SignerKey decode(XdrDataInputStream stream) throws IOException {
  SignerKey decodedSignerKey = new SignerKey();
  SignerKeyType discriminant = SignerKeyType.decode(stream);
  decodedSignerKey.setDiscriminant(discriminant);
  switch (decodedSignerKey.getDiscriminant()) {
  case SIGNER_KEY_TYPE_ED25519:
  decodedSignerKey.ed25519 = Uint256.decode(stream);
  break;
  case SIGNER_KEY_TYPE_PRE_AUTH_TX:
  decodedSignerKey.preAuthTx = Uint256.decode(stream);
  break;
  case SIGNER_KEY_TYPE_HASH_X:
  decodedSignerKey.hashX = Uint256.decode(stream);
  break;
  }
    return decodedSignerKey;
  }
}

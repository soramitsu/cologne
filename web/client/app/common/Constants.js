export const ganacheAddresses = {
  cologneDaoAddress: "0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af",
  userTokenAddress: "0x7d73424a8256c0b2ba245e5d5a3de8820e45f390",
  timeProviderAddress: "0x6e05f58eedda592f34dd9105b1827f252c509de0",
};

export const gorliAddresses = {
  cologneDaoAddress: "0xb34b03cf0930f347529d2b6625f86c34938af807",
  userTokenAddress: "0xf4547f8481a99a12478301ecba1c68f734d37717",
  timeProviderAddress: "0x5fa9b8e645cbe2f23bf04b6d18ba66b1452ea3c8",
};

export const networkMapper = (chainId) => {
  switch (chainId) {
    case "0x5":
      return gorliAddresses;
    default:
      return ganacheAddresses;
  }
};

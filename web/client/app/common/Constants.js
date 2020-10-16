export const ganacheAddresses = {
  cologneDaoAddress: "0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af",
  userTokenAddress: "0x7d73424a8256c0b2ba245e5d5a3de8820e45f390",
  timeProviderAddress: "0x6e05f58eedda592f34dd9105b1827f252c509de0",
};

export const gorliAddresses = {
  cologneDaoAddress: "0x893ade9859be78ef78fe7f52f63138e3a44bbbea",
  userTokenAddress: "0x28c3afa4430aed6612083ac28658543dc48bb984",
  timeProviderAddress: "0xbd25b57e3a95e42b0c8133af4c41a87bba23561d",
};

export const networkMapper = (chainId) => {
  switch (chainId) {
    case "0x5":
      return gorliAddresses;
    default:
      return ganacheAddresses;
  }
};

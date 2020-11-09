export const ganacheAddresses = {
  cologneDaoAddress: "0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af",
  userTokenAddress: "0x7d73424a8256c0b2ba245e5d5a3de8820e45f390",
  timeProviderAddress: "0x6e05f58eedda592f34dd9105b1827f252c509de0",
};

export const gorliAddresses = {
  cologneDaoAddress: "0xa0230bc592afef6f6f52974de25bdd7c1dfc9d9a",
  userTokenAddress: "0xf72e099c9838f328abfa7c3d2bcedd186f36dff0",
  timeProviderAddress: "0x006f0d29713d5399fc2794a713d40612532f9e03",
};

export const networkMapper = (chainId) => {
  switch (chainId) {
    case "0x5":
      return gorliAddresses;
    default:
      return ganacheAddresses;
  }
};

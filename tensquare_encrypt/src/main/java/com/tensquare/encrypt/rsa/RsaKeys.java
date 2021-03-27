package com.tensquare.encrypt.rsa;

/**
 * rsa加解密用的公钥和私钥
 * @author Administrator
 *
 */
public class RsaKeys {

	//生成秘钥对的方法可以参考这篇帖子
	//https://www.cnblogs.com/yucy/p/8962823.html

//	//服务器公钥
//	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Dw9nwjBmDD/Ca1QnRGy"
//											 + "GjtLbF4CX2EGGS7iqwPToV2UUtTDDemq69P8E+WJ4n5W7Iln3pgK+32y19B4oT5q"
//											 + "iUwXbbEaAXPPZFmT5svPH6XxiQgsiaeZtwQjY61qDga6UH2mYGp0GbrP3i9TjPNt"
//											 + "IeSwUSaH2YZfwNgFWqj+y/0jjl8DUsN2tIFVSNpNTZNQ/VX4Dln0Z5DBPK1mSskd"
//											 + "N6uPUj9Ga/IKnwUIv+wL1VWjLNlUjcEHsUE+YE2FN03VnWDJ/VHs7UdHha4d/nUH"
//											 + "rZrJsKkauqnwJsYbijQU+a0HubwXB7BYMlKovikwNpdMS3+lBzjS5KIu6mRv1GoE"
//											 + "vQIDAQAB";
//
//	//服务器私钥(经过pkcs8格式处理)
//	private static final String serverPrvKeyPkcs8 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDoPD2fCMGYMP8J"
//				 								 + "rVCdEbIaO0tsXgJfYQYZLuKrA9OhXZRS1MMN6arr0/wT5YniflbsiWfemAr7fbLX"
//				 								 + "0HihPmqJTBdtsRoBc89kWZPmy88fpfGJCCyJp5m3BCNjrWoOBrpQfaZganQZus/e"
//				 								 + "L1OM820h5LBRJofZhl/A2AVaqP7L/SOOXwNSw3a0gVVI2k1Nk1D9VfgOWfRnkME8"
//				 								 + "rWZKyR03q49SP0Zr8gqfBQi/7AvVVaMs2VSNwQexQT5gTYU3TdWdYMn9UeztR0eF"
//				 								 + "rh3+dQetmsmwqRq6qfAmxhuKNBT5rQe5vBcHsFgyUqi+KTA2l0xLf6UHONLkoi7q"
//				 								 + "ZG/UagS9AgMBAAECggEBANP72QvIBF8Vqld8+q7FLlu/cDN1BJlniReHsqQEFDOh"
//				 								 + "pfiN+ZZDix9FGz5WMiyqwlGbg1KuWqgBrzRMOTCGNt0oteIM3P4iZlblZZoww9nR"
//				 								 + "sc4xxeXJNQjYIC2mZ75x6bP7Xdl4ko3B9miLrqpksWNUypTopOysOc9f4FNHG326"
//				 								 + "0EMazVaXRCAIapTlcUpcwuRB1HT4N6iKL5Mzk3bzafLxfxbGCgTYiRQNeRyhXOnD"
//				 								 + "eJox64b5QkFjKn2G66B5RFZIQ+V+rOGsQElAMbW95jl0VoxUs6p5aNEe6jTgRzAT"
//				 								 + "kqM2v8As0GWi6yogQlsnR0WBn1ztggXTghQs2iDZ0YkCgYEA/LzC5Q8T15K2bM/N"
//				 								 + "K3ghIDBclB++Lw/xK1eONTXN+pBBqVQETtF3wxy6PiLV6PpJT/JIP27Q9VbtM9UF"
//				 								 + "3lepW6Z03VLqEVZo0fdVVyp8oHqv3I8Vo4JFPBDVxFiezygca/drtGMoce0wLWqu"
//				 								 + "bXlUmQlj+PTbXJMz4VTXuPl1cesCgYEA6zu5k1DsfPolcr3y7K9XpzkwBrT/L7LE"
//				 								 + "EiUGYIvgAkiIta2NDO/BIPdsq6OfkMdycAwkWFiGrJ7/VgU+hffIZwjZesr4HQuC"
//				 								 + "0APsqtUrk2yx+f33ZbrS39gcm/STDkVepeo1dsk2DMp7iCaxttYtMuqz3BNEwfRS"
//				 								 + "kIyKujP5kfcCgYEA1N2vUPm3/pNFLrR+26PcUp4o+2EY785/k7+0uMBOckFZ7GIl"
//				 								 + "FrV6J01k17zDaeyUHs+zZinRuTGzqzo6LSCsNdMnDtos5tleg6nLqRTRzuBGin/A"
//				 								 + "++xWn9aWFT+G0ne4KH9FqbLyd7IMJ9R4gR/1zseH+kFRGNGqmpi48MS61G0CgYBc"
//				 								 + "PBniwotH4cmHOSWkWohTAGBtcNDSghTRTIU4m//kxU4ddoRk+ylN5NZOYqTxXtLn"
//				 								 + "Tkt9/JAp5VoW/41peCOzCsxDkoxAzz+mkrNctKMWdjs+268Cy4Nd09475GU45khb"
//				 								 + "Y/88qV6xGz/evdVW7JniahbGByQhrMwm84R9yF1mNwKBgCIJZOFp9xV2997IY83S"
//				 								 + "habB/YSFbfZyojV+VFBRl4uc6OCpXdtSYzmsaRcMjN6Ikn7Mb9zgRHR8mPmtbVfj"
//				 								 + "B8W6V1H2KOPfn/LAM7Z0qw0MW4jimBhfhn4HY30AQ6GeImb2OqOuh3RBbeuuD+7m"
//				 								 + "LpFZC9zGggf9RK3PfqKeq30q";

	//服务器公钥
	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6H+8htDEwS0Lqw38zeyk\n" +
			"9+VrfnxKLGj3n01fsLGxIXx9qVELYAF01KvsxogtG6cfhob43jnE7uzrJSWsz/6f\n" +
			"NjDPWvk+K+MIGwrRs5CjbOZijwVTEUh41Atfly26KT4v+p8cjzVUZgF8AWfok8ZT\n" +
			"pcy8NEuL2iNm5nUmM8d8fESpkF286DqI9ZOJ07DXfwNA2d6PC9KH8BZlMXXuh0al\n" +
			"LgWUDVYUZo//CoyQkMjdzobTCRlcgMl0VP+KkEXxm+Rw+wpSrIu8f2DUq+padavq\n" +
			"HsaRjjcNAsk3036+/9fHZBzPES52WuCikUCJCIS0y15vcaGmHxnb5pNomQgfm0JY\n" +
			"AQIDAQAB";

	//服务器私钥(经过pkcs8格式处理)
	private static final String serverPrvKeyPkcs8 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDof7yG0MTBLQur\n" +
			"DfzN7KT35Wt+fEosaPefTV+wsbEhfH2pUQtgAXTUq+zGiC0bpx+GhvjeOcTu7Osl\n" +
			"JazP/p82MM9a+T4r4wgbCtGzkKNs5mKPBVMRSHjUC1+XLbopPi/6nxyPNVRmAXwB\n" +
			"Z+iTxlOlzLw0S4vaI2bmdSYzx3x8RKmQXbzoOoj1k4nTsNd/A0DZ3o8L0ofwFmUx\n" +
			"de6HRqUuBZQNVhRmj/8KjJCQyN3OhtMJGVyAyXRU/4qQRfGb5HD7ClKsi7x/YNSr\n" +
			"6lp1q+oexpGONw0CyTfTfr7/18dkHM8RLnZa4KKRQIkIhLTLXm9xoaYfGdvmk2iZ\n" +
			"CB+bQlgBAgMBAAECggEAMAx2AcaTYvjUOH2jiUsmrLSVCobVLWhkBGARLosKeEkq\n" +
			"DxvYpfanFmyRelYaPGTFdx/dvs9hi58a2Yp3tGSCsshTkhGAb8Fzo2MRA3MY0yCp\n" +
			"rbUD/cZkci4Hi06/8lYPxFOtq0ryLkKQRhobTq7mCpXNL2GNfX9jfK0pP/oyF9wT\n" +
			"+C+kffTGH5qgax4/N8u0jOU0ST+m+YmZrl5aAJeSuoy5e2u7W0Rbbglumbl/oEqc\n" +
			"rK3m8SXHDYIv8QABz5FxwfOP4bIcPDIh/70x7ACErEs82SaAvJ+BRvqXktwULKG/\n" +
			"YAp56qaQc9B5J8JIeapbSgmjhX+M+8SsGLRo1h33gQKBgQD0Gz0mRpL0PqNwqNHj\n" +
			"ayWHo3bmDyqNO+Mmo2/dSjzSWGBLTKD8HCtnlepQxTTeVVgjgSWRbOJ/Wx4EjZzv\n" +
			"D8Vy/FvF8Cof+upIfzFcqWLmMNbOgjiVyzbi/N7Ic54dFaZp7o4pzz5QISQJ5Pyl\n" +
			"696aeXDtpx7BlnryrbMVo9uD3QKBgQDz07ePRHwbNaNvGQ2iKIbYpyzRFkmemBBn\n" +
			"r0oa75aweDUpJZ4iShiOo97yxOxHkS6+Nj/vYuVw6Cp0j5b2dWC+bA7WH/3Z8OmA\n" +
			"fv8fMWYvsIvw7piDjqocdyM90PUIpg2Huo7fbO3eagueSKoTfGcXRJwgXeoeyVXa\n" +
			"05UfirQkdQKBgAlViU8PX8srIgDK08vIaxT2sTM23T+tTJBLq5lgn1bDvlriwT0i\n" +
			"rp41Y1JXWCAwT0B8Tb1z1m1cRJeT+q5BgiFVeQTmnBXA+HhxNKgAaiG3OF+JJzTD\n" +
			"IoQnezVbBm7CRyzBzHnYzN8vhTtDoE2P6WEqx6CuxURNxsHE1pY7P8eZAoGBAOp3\n" +
			"ZCOL2sAbk17pO5f82rcKA2nIweCVH+lkwaEnVcO6xHwd5XPZxsWbxW+0d/XWIZnd\n" +
			"9xtPAY3SY8kUrzmu4+oZZSb5OpXSOehLty8axMYFQnzcI6MWlHStCCGQh6DadVZY\n" +
			"yr0FNbRUS3nZLFtynHveiTgCt3kFaN716YecTOBlAoGBAMGNKpKI5E+zsimtVBQK\n" +
			"YvKsbNjbcutL6jJq3nwNyXYi1z0J/2VCgXRFy30msn6UtSmsoYNumj18NrDT0X6w\n" +
			"mP8DTAO8ApgG1kH2Agyaqwsg/4PULNyjdbezv2NgTDN6w+Zt2Gfj+at2XrxTtMgD\n" +
			"2DArBgN2D8cmkoGW/MG28+lb";

	public static String getServerPubKey() {
		return serverPubKey;
	}

	public static String getServerPrvKeyPkcs8() {
		return serverPrvKeyPkcs8;
	}

}

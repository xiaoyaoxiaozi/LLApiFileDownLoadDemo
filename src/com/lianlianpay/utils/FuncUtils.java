package com.lianlianpay.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 公共处理组件
 * @author shmily
 * @date May 25, 2011 2:35:38 PM
 */
public class FuncUtils{
    /**
     * 获取唯一标识uuid
     * 
     * @return
     */
    public static String getUuid()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    /**
     * 全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * 
     * 将字符串中的全角字符转为半角
     * @param src 要转换的包含全角的任意字符串
     * @return  转换之后的字符串
     */
    public static String toDBC(String src)
    {
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++)
        {
            if (c[index] == 12288)
            {// 全角空格
                c[index] = (char) 32;
            } else if (c[index] > 65280 && c[index] < 65375)
            {// 其他全角字符
                c[index] = (char) (c[index] - 65248);
            }
        }
        return String.valueOf(c);
    }

    /***************************************************************************
     * 方法名:getSysDate 功能描述:取系统日期 参数说明: 返回参数:返回系统日期串 编写:yajs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String getSysDate()
    {
        SimpleDateFormat formdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date curDate = new java.util.Date();
        String ss;
        ss = formdate.format(curDate);
        return ss;
    }

 

    /***************************************************************************
     * 方法名:getSysTime 功能描述:取系统时间 参数说明: 返回参数:返回系统时间串 编写:yaojs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String getSysTime()
    {
        SimpleDateFormat formdate = new SimpleDateFormat("HH:mm:ss");
        java.util.Date curDate = new java.util.Date();
        String ss;
        ss = formdate.format(curDate);
        return ss;
    }

    /***************************************************************************
     * 方法名:WriteFile 功能描述:写文件 参数说明: 返回参数:返回是否写成功 编写:yaojs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static boolean WriteFile(String fileName, String StrBuf)
            throws IOException
    {
        File file = new File(fileName);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(StrBuf, 0, StrBuf.length());
        bw.newLine();
        bw.close();
        fw.close();
        return true;
    }

    /***************************************************************************
     * 方法名:FormatStringAddBlank 功能描述:给字符串后补空格 参数说明: 返回参数:返回后补的字符串 编写:yaojs
     * 日期:2006.12.13 修改记录:
     * 
     * @throws UnsupportedEncodingException
     **************************************************************************/
    public static String FormatStringAddBlank(String sReturnBuf, int length)
            throws Exception
    {
        StringBuffer tempBuffer = new StringBuffer();
        if (null == sReturnBuf || sReturnBuf.equals("")
                || sReturnBuf.equals("null"))
        {
            for (int i = 0; i < length; i++)
            {
                tempBuffer.append(" ");
            }
            return tempBuffer.toString();
        }
        String s2 = new String(sReturnBuf.getBytes("GB2312"), "ISO8859_1");
        int iLength = s2.length();
        if (length > iLength)
        {
            tempBuffer.append(sReturnBuf);
            for (int j = 0; j < length - iLength; j++)
            {
                tempBuffer.append(" ");
            }
            sReturnBuf = tempBuffer.toString();
        } else if (length < iLength)
        {
            sReturnBuf = absoluteSubstring(sReturnBuf, 0, length);
        }
        return sReturnBuf;
    }

    /***************************************************************************
     * 方法名:getStringLength 功能描述:获取字符的长度,包括汉字的长度 参数说明: 返回参数:返回长度 编写:yaojs
     * 日期:2006.12.13 修改记录:
     * 
     * @throws UnsupportedEncodingException
     **************************************************************************/
    public static int getStringLength(String s1) throws Exception
    {
        if (null == s1 || s1.equals(""))
            return 0;
        String s2 = new String(s1.getBytes("GB2312"), "ISO8859_1");
        return s2.length();
    }

    /***************************************************************************
     * 方法名:FormatStringAddZero 功能描述:给字符串前补0 参数说明: 返回参数:返回前补的字符串 编写:yaojs
     * 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String FormatStringAddZero(String sReturnBuf, int length)
    {
        StringBuffer tempBuffer = new StringBuffer();
        if (null == sReturnBuf || sReturnBuf.equals("")
                || sReturnBuf.equals("null"))
        {
            for (int i = 0; i < length; i++)
            {
                tempBuffer.append("0");
            }
            return tempBuffer.toString();
        }
        int iLength = sReturnBuf.length();
        if (length > iLength)
        {
            for (int j = 0; j < length - iLength; j++)
            {
                sReturnBuf = "0" + sReturnBuf;
            }
        } else if (length < iLength)
        {
            sReturnBuf = absoluteSubstring(sReturnBuf, 0, length);
        }
        return sReturnBuf;
    }

    /***************************************************************************
     * 方法名:MultString 功能描述:将字符串金额变为*100后的金额 参数说明:sMoney：需要变更的字符串， iEr：需要乘的数
     * flag：+代表乘,-代表除 返回参数:返回前补的字符串 编写:yaojs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String MultString(String sMoney, int iEr, String flag)
    {
        if (null == sMoney || sMoney.equals(""))
            return "";
        if (null == flag || flag.equals(""))
            return "";
        sMoney = stringMoveZero(sMoney);
        double iTemp = new Double(sMoney).doubleValue();
        if (flag.equals("+"))
        {
            iTemp = iTemp * iEr;
            int aa = new Double(iTemp).intValue();
            return String.valueOf(aa);
        } else if (flag.equals("-"))
        {
            iTemp = iTemp / iEr;
        }
        return String.valueOf(iTemp);
    }

    /***************************************************************************
     * 方法名:MultStringExt 功能描述:将字符串金额变为*100后的金额 参数说明:sMoney：需要变更的字符串， iEr：需要乘的数
     * flag：+代表乘,-代表除 返回参数:返回前补的字符串 编写:yaojs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String MultStringExt(String sMoney, int iEr, String flag)
    {
        if (null == sMoney || sMoney.equals(""))
            return "";
        if (null == flag || flag.equals(""))
            return "";
        sMoney = stringMoveZero(sMoney);
        double iTemp = new Double(sMoney).doubleValue();
        if (flag.equals("+"))
        {
            iTemp = iTemp * iEr;
            double aa = new Double(iTemp).doubleValue();
            return String.valueOf(aa);
        } else if (flag.equals("-"))
        {
            iTemp = iTemp / iEr;
        }
        return String.valueOf(iTemp);
    }

    /***************************************************************************
     * 方法名:stringMoveZero 功能描述:将字符串金额中负号前的0去掉 参数说明:sMoney：需要变更的字符串， 返回参数:返回的字符串
     * 编写:yaojs 日期:2006.12.13 修改记录:
     **************************************************************************/
    public static String stringMoveZero(String sMoney)
    {
        if (null == sMoney || sMoney.equals(""))
            return "";
        int ilen = sMoney.indexOf("-");
        if (ilen > 0)
            return sMoney.substring(ilen, sMoney.length());
        return sMoney;
    }

    /**
     * 对一个字符串的绝对长度进行拆解(如果遇到汉字字符会把它当作两个字符处理)
     * 
     * @param s
     *            传入的字串
     * @param start
     *            起始绝对位置
     * @param end
     *            终止绝对位置
     * @return 返回的字串 编写:yaojs 日期:2006.12.13
     */
    public static String absoluteSubstring(String s, int start, int end)
    {
        if (s == null || s.equals(""))
        {
            return "";
        }
        try
        {
            String s2 = new String(s.getBytes("GB2312"), "ISO8859_1");
            s2 = s2.substring(start, end);
            return new String(s2.getBytes("ISO8859_1"), "GB2312");
        } catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 对返回的多条记录的字符串进行处理，
     * 
     * @param s
     *            传入的字串
     * @param 单个记录的长度
     * @return 返回字符数组 编写:yaojs 日期:2006.12.13
     * @throws Exception
     */
    public static String[] getAbsoluteSubstringArray(String s, int ilength)
            throws Exception
    {
        if (s == null || s.equals(""))
        {
            return new String[0];
        }
        try
        {
            String s2 = new String(s.getBytes("GB2312"), "ISO8859_1");
            int ilen = s2.length() / ilength;
            if (ilen == 0)
                return new String[0];
            int start = 0;
            int end = ilength;
            String[] returnarray = new String[ilen];
            for (int i = 0; i < ilen; i++)
            {
                String s1 = s2.substring(start, end);
                start = end;
                end = end + ilength;
                returnarray[i] = new String(s1.getBytes("ISO8859_1"), "GB2312");
            }
            return returnarray;
        } catch (Exception e)
        {
            return new String[0];
        }
    }

    /**
     * 对返回的多条记录的字符串进行处理，
     * 
     * @param s
     *            传入的字串
     * @param iLength
     *            短信文本的长度
     * @param 记录数记录本条数据的长度
     * @return 返回字符数组 编写:yaojs 日期:2006.12.13
     * @throws Exception
     */
    public static String[] getAbsoluteSubstringArrayExt(String s, int ilength,
            int extlen) throws Exception
    {
        if (s == null || s.equals(""))
        {
            return new String[0];
        }
        try
        {
            String s2 = new String(s.getBytes("GB2312"), "ISO8859_1");
            int zlen = getStringLength(s);
            int start = 0;
            int end = 0;
            int iRow = new Integer(s2.substring(start, 2)).intValue();
            String[] returnarray = new String[iRow];
            start = start + 2;
            for (int i = 0; i < iRow; i++)
            {
                if (start >= zlen)
                    break;
                int ilen = new Integer(s2.substring(start, start + ilength))
                        .intValue();
                end = start + extlen + ilen;
                String s1 = s2.substring(start, end);
                start = end;
                returnarray[i] = new String(s1.getBytes("ISO8859_1"), "GB2312");
            }
            return returnarray;
        } catch (Exception e)
        {
            return new String[0];
        }
    }

    /**
     * 去除字符串中的所有的空格
     * @param str
     * @return 去除后的字符串
     */
    public static String trimAllBlank(String str)
    {
        if (str != null)
        {
            return str.replace(" ", "");
        } else
        {
            return null;
        }
    }

    /**
     * 对一个字符串按间隔分解
     * 
     * @param fieldsru
     *            传入的字串
     * @param tag
     *            间隔符
     * @return 返回的一个数组 编写:yaojs 日期:2006.12.13
     */
    public static String[] spiltStr(String fieldsru, String tag)
    {
        char dot = tag.charAt(0);
        String field;
        field = fieldsru + dot;
        int num = 0;
        int field_len = field.length();
        for (int i = 0; i < field_len; i++)
        {
            if (field.charAt(i) == dot)
            {
                num++;
            }
        }
        String[] returnarray = new String[num];
        int begin = 0;
        int end;
        for (int j = 0; j < num; j++)
        {
            end = field.indexOf(dot, begin);
            returnarray[j] = field.substring(begin, end);
            begin = end + 1;
        }
        return returnarray;
    }

    /**
     * 对一个字符串判断是否为手机号码
     * 
     * @param checkMobile
     *            传入的字串
     * @return ture or false 编写:yaojs 日期:2006.12.13
     */
    public static boolean checkMobile(String fieldsru)
    {
        if (FuncUtils.isNull(fieldsru))
            return false;
        return Pattern.matches("1[34578][0-9]{9}", fieldsru.trim());
    }

    /**
     * 解密时转换成byte算法时补位
     * 
     * @param strhex
     *            传入的字串符
     * @return byte 编写:yaojs 日期:2006.12.13
     */
    public static byte[] hex2byte(String strhex)
    {
        if (strhex == null)
        {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1)
        {
            return null;
        }

        byte[] b = new byte[l / 2];

        for (int i = 0; i != l / 2; i++)
        {

            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String strchange(String str)
    {
        if (null == str || str.equals("NULL") || str.equals("null"))
        {
            return "";
        } else
            return str;
    }

    public static boolean isNull(String str)
    {
        if (null == str || str.equals("NULL") || str.equals(""))
        {
            return true;
        } else
            return false;
    }

	public static boolean isNotNull(String str) {
		if(StringUtils.isNotBlank(str)){
			return true;
		}
		return false;
	}
 

}

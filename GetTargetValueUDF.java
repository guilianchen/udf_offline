import org.apache.hadoop.hive.ql.exec.UDF;

import org.apache.hadoop.io.Text;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class GetTargetValueUDF extends UDF{
    private static final Gson gson = new Gson();

    public Text evaluate(Text jsonStrText) {
        if (jsonStrText == null) {
            return null;
        }
        String jsonStr = jsonStrText.toString().trim();
        if (jsonStr.isEmpty() || "[]".equals(jsonStr)) {
            return null;
        }

        List<String> valueList = new ArrayList<>();
        try {
            JsonArray jsonArr = gson.fromJson(jsonStr, JsonArray.class);
            for (int i = 0; i < jsonArr.size(); i++) {
                JsonObject itemObj = jsonArr.get(i).getAsJsonObject();
                if (itemObj.has("Value")) {
                    String val = itemObj.get("Value").getAsString();
                    if (val != null) {
                        valueList.add(val);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return new Text(String.join(",", valueList));
    }

    // 本地测试入口main函数
    public static void main(String[] args) {
        GetTargetValueUDF udf = new GetTargetValueUDF();

        // 测试用例1：标准业务JSON（你原始业务数据）
        String testJson1 = "[{\"Path\":\"params.uid\",\"Param\":\"\",\"Value\":\"2647263773461096\"},{\"Path\":\"params.agentEmail\",\"Param\":\"\",\"Value\":\"yuanshuxin.123@bytedance.com\"}]";
        Text result1 = udf.evaluate(new Text(testJson1));
        System.out.println("测试用例1-标准JSON输出：" + (result1 == null ? null : result1.toString()));

        // 测试用例2：空数组字符串
        String testJson2 = "[]";
        Text result2 = udf.evaluate(new Text(testJson2));
        System.out.println("测试用例2-空数组输出：" + (result2 == null ? null : result2.toString()));

        // 测试用例3：空字符串
        String testJson3 = "";
        Text result3 = udf.evaluate(new Text(testJson3));
        System.out.println("测试用例3-空字符串输出：" + (result3 == null ? null : result3.toString()));

        // 测试用例4：JSON缺失Value字段
        String testJson4 = "[{\"Path\":\"test\",\"Param\":\"test\"}]";
        Text result4 = udf.evaluate(new Text(testJson4));
        System.out.println("测试用例4-无Value字段输出：" + (result4 == null ? null : result4.toString()));

        // 测试用例5：脏JSON（非法格式，模拟解析异常）
        String testJson5 = "[{\"Path\":\"abc\",Value:\"123\",}";
        Text result5 = udf.evaluate(new Text(testJson5));
        System.out.println("测试用例5-脏JSON输出：" + (result5 == null ? null : result5.toString()));

        // 测试用例6：
        String testJson6 = "[{\"Path\":\"PageContext.IMPageContext.Uid\",\"Param\":\"\",\"Value\":\"67225083231\"},{\"Path\":\"PageContext.PhonePageContxt.Uid\",\"Param\":\"\",\"Value\":\"67225083231\"},{\"Path\":\"PageContext.IMPageContext.MsgUid\",\"Param\":\"\",\"Value\":\"7122014364982345767\"},{\"Path\":\"PageContext.ERSPageContext.Uid\",\"Param\":\"\",\"Value\":\"67225083231\"},{\"Path\":\"PageContext.IMPageContext.BizUid\",\"Param\":\"\",\"Value\":\"67225083231\"}]";
        Text result6 = udf.evaluate(new Text(testJson6));
        System.out.println("测试用例6-输出：" + (result6 == null ? null : result6.toString()));
    }
}
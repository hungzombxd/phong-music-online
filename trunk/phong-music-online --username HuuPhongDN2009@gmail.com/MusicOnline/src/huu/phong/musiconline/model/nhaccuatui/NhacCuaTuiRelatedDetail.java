package huu.phong.musiconline.model.nhaccuatui;
import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiRelatedDetail
{

  @SerializedName("IsMore")
  public Boolean isMore;

  @SerializedName("Data")
  public NhacCuaTuiRelated[] listRelatedObject;

  @SerializedName("Result")
  public Boolean result;
}
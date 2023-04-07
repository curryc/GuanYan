package com.scu.guanyan.utils.ORC;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import com.scu.guanyan.R;

/**
 * Description:  OCR的工具类，建议另起线程，传入context
 * @author: Pu Bowei
 * @date: 2023/3/18 22:25
 * @param:
 * @return:
 */
public class OCRUtils {
    protected Context context;
    protected String modelPath = "";
    protected String labelPath = "";
    protected String imagePath = "";
    protected int cpuThreadNum = 1;
    protected String cpuPowerMode = "";
    protected int detLongSize = 960;
    protected float scoreThreshold = 0.1f;
    private String currentPhotoPath;
    protected Predictor predictor = new Predictor();

    public OCRUtils(Context context) {
        this.context=context;
        initSettings();
    }

    public void initSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String model_path = sharedPreferences.getString(getString(R.string.MODEL_PATH_KEY),
                getString(R.string.MODEL_PATH_DEFAULT));
        String label_path = sharedPreferences.getString(getString(R.string.LABEL_PATH_KEY),
                getString(R.string.LABEL_PATH_DEFAULT));
        String image_path = sharedPreferences.getString(getString(R.string.IMAGE_PATH_KEY),
                getString(R.string.IMAGE_PATH_DEFAULT));
        int cpu_thread_num = Integer.parseInt(sharedPreferences.getString(getString(R.string.CPU_THREAD_NUM_KEY),
                getString(R.string.CPU_THREAD_NUM_DEFAULT)));
        String cpu_power_mode =
                sharedPreferences.getString(getString(R.string.CPU_POWER_MODE_KEY),
                        getString(R.string.CPU_POWER_MODE_DEFAULT));
        int det_long_size = Integer.parseInt(sharedPreferences.getString(getString(R.string.DET_LONG_SIZE_KEY),
                getString(R.string.DET_LONG_SIZE_DEFAULT)));
        float score_threshold =
                Float.parseFloat(sharedPreferences.getString(getString(R.string.SCORE_THRESHOLD_KEY),
                        getString(R.string.SCORE_THRESHOLD_DEFAULT)));


        this.labelPath = label_path;
        this.imagePath = image_path;
        this.detLongSize = det_long_size;
        this.scoreThreshold = score_threshold;
        this.modelPath=model_path;
        this.cpuPowerMode=cpu_power_mode;
        this.cpuThreadNum=cpu_thread_num;

    }

    private String getString(int i){
        return  this.context.getString(i);
    }
    /**
     * Description: 加载模型
     * @author: Pu Bowei
     * @date: 2023/3/19 0:31
     * @param:
     * @return:
     */
    public boolean onLoadModel() {
        if (predictor.isLoaded()) {
            predictor.releaseModel();
        }
        return predictor.init(context, modelPath, labelPath, 0, cpuThreadNum,
                cpuPowerMode,
                detLongSize, scoreThreshold);
    }
    /**
     * Description: 运行模型
     * @author: Pu Bowei
     * @date: 2023/3/19 0:31
     * @param:
     * @return:
     */
    public boolean onRunModel() {
        return predictor.isLoaded() && predictor.runModel(1, 1, 0);
    }
    /**
     * Description: 获取模型的当前输出
     * @author: Pu Bowei
     * @date: 2023/3/19 0:31
     * @param:
     * @return:
     */
    public String getModelRunResult(){
        return predictor.outputResult();
    }

    /**
     * Description:输入图片和运行模型
     * @author: Pu Bowei
     * @date: 2023/3/19 0:25
     * @param: 传入Bitmap
     * @return:
     */
    public boolean setImgAndRunModel(Bitmap image) {
        if (image == null) {
           return  false;
        } else if (!predictor.isLoaded()) {
            return  false;
        } else {
            predictor.setInputImage(image);
            return  onRunModel();
        }
    }
}


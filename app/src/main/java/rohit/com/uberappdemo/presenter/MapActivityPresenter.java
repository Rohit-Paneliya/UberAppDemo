package rohit.com.uberappdemo.presenter;

import rohit.com.uberappdemo.interfaces.IGetDataCallBack;
import rohit.com.uberappdemo.network.DirectionApiCall;

public class MapActivityPresenter {

    private IGetDataCallBack<String> iGetDataCallBack;

    public MapActivityPresenter(IGetDataCallBack<String> iGetDataCallBack) {
        this.iGetDataCallBack = iGetDataCallBack;
    }

    /*
     *   Code separation - View(Activity) is not aware about the data from where it is coming.
     * */
    public void callDirectionAPI(String url) {
        DirectionApiCall.getDirectionDataFromAPI(url, new IGetDataCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                iGetDataCallBack.onSuccess(data);
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}

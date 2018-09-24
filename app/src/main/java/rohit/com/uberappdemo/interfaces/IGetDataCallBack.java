package rohit.com.uberappdemo.interfaces;

/*
 *   Used generic interface for data callback and presenter callback to view.
 * */
public interface IGetDataCallBack<T> {
    void onSuccess(T data);

    void onFailure(T msg);
}

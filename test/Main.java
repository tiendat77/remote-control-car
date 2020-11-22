public class Main {

    public class connectTask extends AsyncTask<String, String, SocketClient> {

        @Override
        protected SocketClient doInBackground(String... message) {

            //we create a Client object and
            mSocket = new SocketClient(new SocketClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, Constants.SERVER_IP);
            mSocket.start();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
}

package com.example.iveci.pmultip;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by iveci on 2017-06-05.
 */

public class MusicAdapter extends BaseAdapter {
    Context context;
    ArrayList<Meta> metaArrayList;

    public MusicAdapter(Context context, ArrayList<Meta> metaArrayList) {
        this.context = context;
        this.metaArrayList = metaArrayList;
    }

    @Override
    public int getCount() {
        return metaArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.music, parent, false);
        }
        TextView song = (TextView) convertView.findViewById(R.id.tsongname);
        TextView artist = (TextView) convertView.findViewById(R.id.tartist);
        song.setText(metaArrayList.get(position).getTitle());
        artist.setText(metaArrayList.get(position).getArtist());

        ImageView aAlbumart = (ImageView) convertView.findViewById(R.id.listalbart);
        Bitmap bAlbumart = getAlbumart(context, Integer.parseInt(metaArrayList.get(position).getAlbumId()), 80);
        aAlbumart.setImageBitmap(bAlbumart);

        return convertView;
    }

    private static final BitmapFactory.Options opt = new BitmapFactory.Options();

    private Bitmap getAlbumart(Context context, int albumid, int imgsize) { //앨범아트를 불러옵니다. 사이즈가 맞지 않는경우 이 안에서 스케일링합니다.
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart" + albumid);
        if(uri != null) {
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = resolver.openFileDescriptor(uri, "r");
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, opt);
                int scale = 0;
                if(opt.outHeight > imgsize || opt.outWidth > imgsize) {
                /* 받아온 이미지의 크기가 맞지 않는 경우 스케일링. 함수 매우 복잡.
                기본스케일을 이미지의 길쭉한 쪽으로 나눈 값의 로그를 구하고
                그걸 또 로그0.5로 나누고 그거를 지수로 삼은 2의 계승?을 구해서 정수로 캐스팅하는데
                왜 이렇게 하는지는 난 모르겠다. 그냥 이렇게 하면 된다고 해서 쓰는것이다. */
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(imgsize / (double) Math.max(opt.outHeight, opt.outWidth)) / Math.log(0.5)));
                }
                opt.inJustDecodeBounds = false;
                opt.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, opt);
                if(bitmap != null && (opt.outHeight != imgsize || opt.outWidth != imgsize)) { //스케일링
                    Bitmap temp = Bitmap.createScaledBitmap(bitmap, imgsize, imgsize, true);
                    bitmap.recycle();
                    bitmap = temp;
                }
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fileDescriptor != null) fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

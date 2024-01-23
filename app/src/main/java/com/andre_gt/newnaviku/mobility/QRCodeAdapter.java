package com.andre_gt.newnaviku.mobility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andre_gt.newnaviku.R;

import java.util.List;

public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder> {

    private List<String> qrCodePaths;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public QRCodeAdapter(List<String> qrCodePaths, OnItemClickListener listener) {
        this.qrCodePaths = qrCodePaths;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QRCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qrcode, parent, false);
        return new QRCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeViewHolder holder, int position) {
        String qrCodePath = qrCodePaths.get(position);
        Bitmap qrCodeBitmap = BitmapFactory.decodeFile(qrCodePath);
        holder.qrCodeImageView.setImageBitmap(qrCodeBitmap);

        // Assuming you have a method to get the translation for the QR Code content
        String translationText = getTranslationForQRCode(qrCodePath);
        holder.translationTextView.setText(translationText);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return qrCodePaths.size();
    }

    static class QRCodeViewHolder extends RecyclerView.ViewHolder {
        ImageView qrCodeImageView;
        TextView translationTextView;

        QRCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeImageView = itemView.findViewById(R.id.qrCodeImageView);
            translationTextView = itemView.findViewById(R.id.translationTextView);
        }
    }

    private String getTranslationForQRCode(String qrCodePath) {
        return "Translation for " + qrCodePath;
    }
}

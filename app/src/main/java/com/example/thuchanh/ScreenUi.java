package com.example.thuchanh;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

final class ScreenUi {
    private ScreenUi() {}
    static void setup(AppCompatActivity activity, int layout) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        activity.setContentView(layout);
        WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView()).setAppearanceLightNavigationBars(true);
        // Chừa khoảng cho thanh hệ thống, vùng khoét và bàn phím, không cộng inset hai lần.
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.root), (view, insets) -> {
            Insets safe = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(activity.findViewById(R.id.root));
    }
    static void avatar(TextView view, Contact contact) {
        String[] colors = {"#00796B", "#5166A8", "#AC613D", "#8554A0", "#326C8A"};
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.parseColor(colors[Math.floorMod(contact.getId().hashCode(), colors.length)]));
        view.setBackground(circle);
        view.setText(contact.getInitial());
        view.setContentDescription("Ảnh đại diện " + contact.getFullName());
    }
}

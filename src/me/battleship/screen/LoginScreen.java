package me.battleship.screen;

import me.battleship.R;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
/**
 * A login screen
 *
 * @author Manuel Vögele
 */
public class LoginScreen implements Screen, OnClickListener
{
	public static final String LOG_TAG = "LoginScreen";
	
	@Override
	public View getView(final Activity activity)
	{
		FrameLayout root = new FrameLayout(activity);
		ViewFactory.<View>createView(R.layout.login, root, activity);
		Button loginButton = (Button) root.findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(this);
		Button anonymousLoginButton = (Button) root.findViewById(R.id.buttonAnonymousLogin);
		anonymousLoginButton.setOnClickListener(this);
		return root;
	}

	@Override
	public void onClick(View v)
	{
		// TODO: Implement onClick action
	}
}

package com.eric.forceengine;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.eric.forceengine.SettingsFragment.OnSettingsInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
	private static final String ARG_RESTITUTION = "restitution";
	private static final String ARG_FRICTION = "friction";
	private static final String ARG_GRAVITY = "gravity";
	private static final String ARG_TRAILS = "trails";

	private SeekBar mRestitutionBar, mFrictionBar;

	private Switch mGravitySwitch, mTrailsSwitch;

	private OnSettingsInteractionListener mListener;

	public SettingsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param restitution the resitution.
	 * @param friction    the friction.
	 * @param gravityEnabled whether or not gravity is enabled
	 * @param trailsEnabled whether or not trails are enabled
	 * @return A new instance of SettingsFragment.
	 */
	public static SettingsFragment newInstance(float restitution, float friction,
	                                           boolean gravityEnabled, boolean trailsEnabled) {
		SettingsFragment fragment = new SettingsFragment();
		Bundle args = new Bundle();
		args.putFloat(ARG_RESTITUTION, restitution);
		args.putFloat(ARG_FRICTION, friction);
		args.putBoolean(ARG_GRAVITY, gravityEnabled);
		args.putBoolean(ARG_TRAILS, trailsEnabled);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

		if (rootView != null) {

			float restitution = ForceEngineActivity.DEFAULT_RESTITUTION;
			float friction = ForceEngineActivity.DEFAULT_FRICTION;
			boolean gravity = ForceEngineActivity.DEFAULT_GRAVITY_ENABLED;
			boolean trails = ForceEngineActivity.DEFAULT_TRAILS_ENABLED;

			if (getArguments() != null) {
				restitution = getArguments().getFloat(ARG_RESTITUTION, ForceEngineActivity.DEFAULT_RESTITUTION);
				friction = getArguments().getFloat(ARG_FRICTION, ForceEngineActivity.DEFAULT_FRICTION);
				gravity = getArguments().getBoolean(ARG_GRAVITY, ForceEngineActivity.DEFAULT_GRAVITY_ENABLED);
				trails = getArguments().getBoolean(ARG_TRAILS, ForceEngineActivity.DEFAULT_TRAILS_ENABLED);
			}

			mRestitutionBar = (SeekBar) rootView.findViewById(R.id.restitution);

			if (mRestitutionBar != null) {
				mRestitutionBar.setProgress((int) (restitution * 100.0f));

				mRestitutionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (mListener != null) {
							mListener.onRestitutionChanged(progress / 100.0f);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
				});
			}

			mFrictionBar = (SeekBar) rootView.findViewById(R.id.friction);

			if (mFrictionBar != null) {
				mFrictionBar.setProgress((int) (friction * 100.0f));

				mFrictionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (mListener != null) {
							mListener.onFrictionChanged(progress / 100.0f);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
				});
			}

			mGravitySwitch = (Switch) rootView.findViewById(R.id.gravity);

			if (mGravitySwitch != null) {
				mGravitySwitch.setChecked(gravity);

				mGravitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (mListener != null) {
							mListener.onGravityEnabledChanged(isChecked);
						}
					}
				});
			}

			mTrailsSwitch = (Switch) rootView.findViewById(R.id.trails);

			if (mTrailsSwitch != null) {
				mTrailsSwitch.setChecked(trails);

				mTrailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (mListener != null) {
							mListener.onTrailsEnabledChanged(isChecked);
						}
					}
				});
			}
		}

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSettingsInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnSettingsInteractionListener {

		/**
		 * @param restitution the energy retained after collisions
		 */
		public void onRestitutionChanged(float restitution);

		/**
		 * @param friction the drag on the circles
		 */
		public void onFrictionChanged(float friction);

		/**
		 * @param gravityEnabled whether or not gravity is enabled
		 */
		public void onGravityEnabledChanged(boolean gravityEnabled);

		/**
		 * @param trailsEnabled whether or not trails are enabled
		 */
		public void onTrailsEnabledChanged(boolean trailsEnabled);
	}

}

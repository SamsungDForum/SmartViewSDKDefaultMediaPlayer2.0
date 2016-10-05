##Prerequisite

###1. [SmartView SDK Android Library](http://www.samsungdforum.com/AddLibrary/SmartViewDownload):  Android Package(Mobile)
	
	added source /libs/android-smartview-sdk-2.3.4.jar

###2. [Picasso](http://repo1.maven.org/maven2/com/squareup/picasso/picasso/2.5.2/picasso-2.5.2.jar):  To handle Image (thumbnail) download
	
	added source /libs/picasso-2.5.2.jar


###3. Android Permmissions
    <!-- Required for fetching feed data. -->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <!-- Required by MSF Library -->
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
    <!-- Required by MSF Library -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <!-- Required by MSF Library [for discovery over BLE]-->
    <uses-permission android:name="android.permission.BLUETOOTH"/>
 
 
## Discover : Search devices around your mobile.
1. Pressing 'Cast' button in ActionBar, must start search API [search.start()].
2. Populate device list by overriding onFound() & onLost() listeners.
3. Stop device discovery, by calling stop search API [search.stop()].

 


	TVSearch.java
	
		private Search mTVSearch = null;
		private TVListAdapter mTVListAdapter = null;
		private Handler mTVLsitHandler = new Handler();
	
		/*
		 * Method to notify TV List data change.
		 */
		private void notifyDataChange() {
			mTVLsitHandler.post(new Runnable() {
				@Override
				public void run() {
					mTVListAdapter.notifyDataSetChanged();
				}
			});
		}

		/*
		 * Method to update (add) new service (tv) to ListView adapter.
		 */
		private void updateTVList(Service service) {
			if(null == service)
			{
				Log.w(TAG, "updateTVList(): NULL service!!!");
				return;
			}

			/*If service already doesn't exist in TVListAdapter, add it*/
			if(!mTVListAdapter.contains(service))
			{
				mTVListAdapter.add(service);
				Log.v(TAG, "TVListAdapter.add(service): " + service);
				notifyDataChange();
			}
		}

		/*Start TV Discovery*/
		public void startDiscovery() {
			if(mContext == null || mTVListAdapter == null) {
				Log.w(TAG, "Can't start Discovery.");
				return;
			}

			if(null == mTVSearch)
			{
				mTVSearch = Service.search(mContext);
				Log.v(TAG, "Device (" + mTVSearch + ") Search instantiated..");
				mTVSearch.setOnServiceFoundListener(new Search.OnServiceFoundListener() {
					@Override
					public void onFound(Service service) {
						Log.v(TAG, "setOnServiceFoundListener(): onFound(): Service Added: " + service);
						updateTVList(service);
					}
				});

				mTVSearch.setOnStartListener(new Search.OnStartListener() {
					@Override
					public void onStart() {
						Log.v(TAG, "Starting Discovery.");
					}
				});

				mTVSearch.setOnStopListener(new Search.OnStopListener() {
					@Override
					public void onStop() {
						Log.v(TAG, "Discovery Stopped.");
					}
				});

				mTVSearch.setOnServiceLostListener(new Search.OnServiceLostListener() {
					@Override
					public void onLost(Service service) {
						Log.v(TAG, "Discovery: Service Lost!!!");
						/*remove TV*/
						if (null == service) {
							return;
						}
						mTVListAdapter.remove(service);
						notifyDataChange();
					}
				});
			}

			boolean bStartDiscovery = mTVSearch.start();
			if(bStartDiscovery)
			{
				Log.v(TAG, "Discovery Already Started..");
			}
			else
			{
				Log.v(TAG, "New Discovery Started..");
			}
		}
		
		/* Stop TV Discovery*/
		public void stopDiscovery() {
			if (null != mTVSearch)
			{
				mTVSearch.stop();
				mTVSearch = null;
				Log.v(TAG, "Stopping Discovery.");
			}
		}


## Create [Video|Audio|Photo]Player object and launch a TV application.

1. Get 'service' instance from devices list.
2. Create an application instance using [Video|Audio|Photo]Player.
3. Now, content on the TV can be launched by calling player's respective API - PlayContent(final Uri contentUrl, [<Additional Parameters>,] final Result<Boolean> result).
Note: All players support PlayContent API but with their respective argument set. Below example shows use of Audio Player APIs.



	MainActivity.java/MediaLauncherSingleton.java
	
		private static AudioPlayer mAudioPlayer = null;
	
		lstConnectedTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*Get selected TV's service object*/
				mService = (Service) parent.getItemAtPosition(position);
				/*Dismiss TV List Dialog*/
				lstDialog.dismiss();
				/*Set service for the app*/
				mAudioPlayer = this.mService.createAudioPlayer(playerName);

				/*Stop discovery*/
				mTVSearch.stopDiscovery();
			}
		});


    MediaLauncherSingleton.java
    
        mAudioPlayer.playContent(Uri.parse(uri),
                    title,
                    albumName,           /*albumName*/
                    Uri.parse(albumArt), /*albumArtUrl*/
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            Log.v(TAG, "playContent(): onSuccess.");
                            mPlayerType = PlayerType.AUDIO;
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            Log.v(TAG, "playContent(): onError: " + error.getMessage());
                            Toast.makeText(mContext, "Error in Launching Content!", Toast.LENGTH_SHORT).show();
                        }
                    });
 


##Event Handling
1. Events for successful player's & client's connection/disconnection can be handled via overloading respective listeners.
2. To handle player events from TV, use API - addOnMessageListener().
 

    MediaLauncherSingleton.java

        mAudioPlayer.setOnConnectListener(new Channel.OnConnectListener() {
            @Override
            public void onConnect(Client client) {
                Log.v(TAG, "setOnConnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.CONNECTED);
            }
        });

        mAudioPlayer.setOnDisconnectListener(new Channel.OnDisconnectListener() {
            @Override
            public void onDisconnect(Client client) {
                resetService();
                Log.v(TAG, "setOnDisconnectListener() called!");
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
                SwitchAppScreen.getInstance(mContext).destroy();
            }
        });

        mAudioPlayer.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "setOnErrorListener() called: Error: " + error.getMessage());
                ConnectStateMachineSingleton.getInstance().setCurrentConnectState(ConnectStates.DISCONNECTED);
            }
        });
        
		AudioPlayer.OnAudioPlayerListener audioPlayerListener = new AudioPlayer.OnAudioPlayerListener() {
            @Override
            public void onBufferingStart() {
                Log.v(TAG, "PlayerNotice: onBufferingStart");
                PlaybackControls.getInstance(mContext).onMediaBufferingStart();
            }

            @Override
            public void onBufferingComplete() {
                Log.v(TAG, "PlayerNotice: onBufferingComplete");
                PlaybackControls.getInstance(mContext).onMediaBufferingComplete();
            }

            @Override
            public void onBufferingProgress(int progress) {
                Log.v(TAG, "PlayerNotice: onBufferingProgress: " + progress);
                PlaybackControls.getInstance(mContext).onMediaBufferingProgress(progress);
            }

            @Override
            public void onCurrentPlayTime(int progress) {
                Log.v(TAG, "PlayerNotice: onCurrentPlayTime: " + progress);
                PlaybackControls.getInstance(mContext).onMediaCurrentPlayTime(progress);
            }

            @Override
            public void onStreamingStarted(int duration) {
                Log.v(TAG, "PlayerNotice: onStreamingStarted: " + duration);
                PlaybackControls.getInstance(mContext).onMediaVideoStreamStart(duration);
                getControlStatus();
            }

            @Override
            public void onStreamCompleted() {
                Log.v(TAG, "PlayerNotice: onStreamCompleted");
                PlaybackControls.getInstance(mContext).onMediaStreamCompleted();
            }

            @Override
            public void onPlay() {
                Log.v(TAG, "PlayerNotice: onPlay");
                PlaybackControls.getInstance(mContext).onMediaPlay();
            }

            @Override
            public void onPause() {
                Log.v(TAG, "PlayerNotice: onPause");
                PlaybackControls.getInstance(mContext).onMediaPause();
            }

            @Override
            public void onStop() {
                Log.v(TAG, "PlayerNotice: onStop");
                PlaybackControls.getInstance(mContext).onMediaStop();
            }

            @Override
            public void onMute() {
                Log.v(TAG, "PlayerNotice: onMute");
                PlaybackControls.getInstance(mContext).onMediaMute();
            }

            @Override
            public void onUnMute() {
                Log.v(TAG, "PlayerNotice: onUnMute");
                PlaybackControls.getInstance(mContext).onMediaUnMute();
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Log.v(TAG, "PlayerNotice: onError: " + error.getMessage());
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToList(JSONObject enqueuedItem) {
                Log.v(TAG, "PlayerNotice: onEnqueue: " + enqueuedItem.toString());
                QueueSingleton.getInstance(mContext).onEnqueue(enqueuedItem, mPlayerType);
            }

            @Override
            public void onRemoveFromList(JSONObject dequeuedItem) {
                Log.v(TAG, "PlayerNotice: onDequeue: " + dequeuedItem.toString());
                QueueSingleton.getInstance(mContext).onDequeue(dequeuedItem);
            }

            @Override
            public void onClearList() {
                Log.v(TAG, "PlayerNotice: onQueueClear");
                QueueSingleton.getInstance(mContext).onClearQueue();
            }

            @Override
            public void onGetList(JSONArray queueList) {
                Log.v(TAG, "PlayerNotice: onQueueFetch: " + queueList.toString());
                QueueSingleton.getInstance(mContext).onFetchQueue(queueList, mPlayerType);
            }

            @Override
            public void onCurrentPlaying(JSONObject currentItem, String playerType) {
                Log.v(TAG, "PlayerNotice: onCurrentPlaying: " + currentItem.toString());
                PlaybackControls.getInstance(mContext).onCurrentPlaying(currentItem, playerType);
                //Stop loader when 1st item starts playing.
                Loader.getInstance(mContext).destroy();
                SwitchAppScreen.getInstance(mContext).destroy();
            }

            @Override
            public void onShuffle(Boolean state) {
                Log.v(TAG, "PlayerNotice: onShuffle: " + state.toString());
                PlaybackControls.getInstance(mContext).onShuffle(state);
            }

            @Override
            public void onRepeat(VideoPlayer.RepeatMode mode) {
                Log.v(TAG, "PlayerNotice: onRepeat: " + mode.toString());
                PlaybackControls.getInstance(mContext).onRepeat(mode);
            }

            @Override
            public void onControlStatus(int volLevel, Boolean muteStatus, Boolean shuffleStatus, VideoPlayer.RepeatMode repeatStatus) {
                Log.v(TAG, "PlayerNotice: onControlStatus A: vol: " + volLevel + ", mute: " + muteStatus + ", shuffle: " + shuffleStatus + ", repeat: " + repeatStatus.name());
                PlaybackControls.getInstance(mContext).onControlStatus(volLevel, muteStatus, shuffleStatus, repeatStatus);
            }

            @Override
            public void onVolumeChange(int level) {
                Log.v(TAG, "PlayerNotice: onVolumeChange: " + level);
                PlaybackControls.getInstance(mContext).onVolumeChange(level);
            }

            @Override
            public void onPlayerInitialized() {
                Log.v(TAG, "PlayerNotice: onPlayerInitialized");
            }

            @Override
            public void onPlayerChange(String playerType) {
                Log.v(TAG, "PlayerNotice: onPlayerChange");
                //display loader if user adds another list, till list is fetched by the app.
                Loader.getInstance(mContext).display();
                //reset all playback controls.
                PlaybackControls.getInstance(mContext).resetPlaybackControls();
                //update all controls.
                getControlStatus();
            }

            @Override
            public void onApplicationResume() {
                Log.v(TAG, "PlayerNotice: onApplicationResume");
                SwitchAppScreen.getInstance(mContext).destroy();
                getControlStatus();
            }

            @Override
            public void onApplicationSuspend() {
                Log.v(TAG, "PlayerNotice: onApplicationSuspend");
                SwitchAppScreen.getInstance(mContext).display();
            }
        };
        mAudioPlayer.addOnMessageListener(audioPlayerListener);
		
        
##API usage
1. Use respective player's APIs to send commands to TV(like play, pause, stop, mute, etc.).
2. List is maintained by TV of playing/to be played content. This list can be curated via APIs like addToList(), removeFromList, etc.

	MediaLauncherSingleton.java
	
		public void play(){
			mAudioPlayer.play();
		}

		public void pause(){
			mAudioPlayer.pause();
		}

		public void stop(){
			mAudioPlayer.stop();
		}

		public void mute(){
			mAudioPlayer.mute();
		}

		public void unmute(){
			mAudioPlayer.unMute();
		}

		public void enqueue(final Uri uri,
							final String title,
							final String albumName,
							final Uri albumArt) {
			mAudioPlayer.addToList(uri, title, albumName, albumArt);
		}

		public void enqueue(final List<Map<String, String>> list, PlayerType playerType) {
			mAudioPlayer.addToList(list);
		}

		public void dequeue(final Uri uri) {
			mAudioPlayer.removeFromList(uri);
		}

		public void fetchQueue() {
			mAudioPlayer.getList();
		}

		public void clearQueue() {
			mAudioPlayer.clearList();
		}

		public void repeatQueue() {
			mAudioPlayer.repeat();
		}

		public void shuffleQueue() {
			mAudioPlayer.shuffle();
		}

		public void seekTo(int progress) {
			mAudioPlayer.seekTo(progress, TimeUnit.MILLISECONDS);
		}

		public void getControlStatus() {
			mAudioPlayer.getControlStatus();
		}

		public void setVolume(int level) {
			mAudioPlayer.setVolume(level);
		}

		public void volumeUp() {
			mAudioPlayer.volumeUp();
		}

		public void volumeDown() {
			mAudioPlayer.volumeDown();
		}

		public void next() {
			mAudioPlayer.next();
		}

		public void previous() {
			mAudioPlayer.previous();
		}

		public void resumeApplicationInForeground() {
			mAudioPlayer.resumeApplicationInForeground();
		}
		
##Handling Player Process State
Launching application(s) on TV (while default media player[DMP] is running on TV) may put DMP in suspended state. To resume DMP as foreground process use API resumeApplicationInForeground().



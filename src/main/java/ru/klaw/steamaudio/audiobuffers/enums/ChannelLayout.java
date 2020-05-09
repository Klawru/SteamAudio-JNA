package ru.klaw.steamaudio.audiobuffers.enums;

import ru.klaw.utility.JnaEnum;

public enum ChannelLayout implements JnaEnum<ChannelLayout> {
        /**
         * A single speaker, typically in front of the user.
         */
        MONO,
        /**
         * A pair of speakers, one to the left of the user, and one to the right.
         * This is also the setting to use when playing audio over headphones.
         */
        STEREO,
        /**
         * Four speakers: front left, front right, back left, and back right.
         */
        QUADRAPHONIC,
        /**
         * Six speakers: front left, front center, front right, back left, back
         * right, and subwoofer.
         */
        FIVEPOINTONE,
        /**
         * Eight speakers: front left, front center, front right, side left, side
         * right, back left, back right, and subwoofer.
         */
        SEVENPOINTONE,
        /**
         * Lets you specify your own speaker configuration. You can specify any
         * number of speakers, and set their positions relative to the user. This
         * is useful if you have a large speaker array, or if you want Phonon to
         * account for the heights at which the speakers have been installed.
         */
        CUSTOM;

        @Override
        public int getIntValue() {
                return ordinal();
        }

        @Override
        public ChannelLayout getForValue(int i) {
                return values()[i];
        }
}

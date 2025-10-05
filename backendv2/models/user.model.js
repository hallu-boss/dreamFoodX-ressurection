import mongoose from "mongoose";

const UserSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: [true, 'User name is required'],
        trim: true,
        minLength: 2,
        maxLength: 30
    },
    surname: { 
        type: String, 
        required: [true, 'User surname is required'],
        trim: true,
        minLength: 2,
        maxLength: 30
    },
    email: { 
        type: String, 
        required: [true, 'User email is required'],
        trim: true,
        lowerCase: true,
        unique: true,
        match: [/\S+@\S+\.\S+/, 'Please fill a valid email address']
    },
    password: { 
        type: String, 
        required: [true, 'User passwrod is required'],
        minLength: 6
    },
    cookingHours: { type: Number, default: 0 },
}, {timestamps: true});

const User = mongoose.model('User', UserSchema);

export default User;

import express from 'express';
import { register, login, getProfile } from '../controllers/auth.controller';
import { authenticate } from '../middleware/authenticate';
import { validateRegistration, validateLogin } from '../middleware/validators';
import { googleAuth } from '../controllers/googleAuth.controller';

const router = express.Router();

router.post('/register', validateRegistration, register);
router.post('/login', validateLogin, login);
router.get('/profile', authenticate, getProfile);
router.post('/google', googleAuth);

export default router;

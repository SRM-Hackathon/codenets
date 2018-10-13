from keras.preprocessing.image import img_to_array
from keras.models import load_model
import numpy as np
import argparse
import imutils
import cv2
#from flask import Flask, render_template
import flask
from flask import Flask, request, Response
from PIL import Image
import io
import jsonpickle
from keras import backend as K
import os
from werkzeug import secure_filename


app = flask.Flask(__name__)
app.config["DEBUG"] = True

# Initialize the Flask application
app = Flask(__name__)


# route http posts to this method
@app.route('/api/test', methods=['POST'])
def test():
    if request.method == 'POST':
        file = request.files['uploaded_file']
        filename = file.filename
        file.save(secure_filename(filename))
        # r = request
	    # image = Image.open(io.BytesIO(r.data))
	    # image.save('meow.jpeg')
        im = cv2.imread(filename, 1)
        imagee = cv2.resize(im, (150, 150))
        print("The image shape is ", imagee.shape)
        imagee = imagee.astype("float") / 255.0
        imagee = img_to_array(imagee)
        imagee = np.expand_dims(imagee, axis=0)
        model = load_model('model.h5')
        pred_vals = model.predict(imagee)
        print(pred_vals)
        if (pred_vals[0][0]>pred_vals[0][1]):
            print ('You are Normal with a probabality score of :', str(pred_vals[0][0]*100))
            result = 'You are Normal with a probabality score of :', str(pred_vals[0][0]*100)
        else:
            print('You have been diagnosed with Pneumonia on a Probabality score of:', str(pred_vals[0][1]*100))
            result = 'You have been diagnosed with Pnuemonia on a probabality score of :', str(pred_vals[0][1]*100)
    
    else:
        print("Error")


    #nparr = np.fromstring(r.data, np.uint8)
    # decode image
    #img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # do some fancy processing here....

    # build a response dict to send back to client



    response = {'RESULT': '{}'.format(result)
                }
    # encode response using jsonpickle
    response_pickled = jsonpickle.encode(response)
    K.clear_session()
    os.remove(filename)

    return Response(result, status=200, mimetype="application/json")

# start flask app
app.run(host="0.0.0.0", port=5000)

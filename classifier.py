import numpy as np
from sklearn.linear_model.stochastic_gradient import SGDRegressor
from sklearn.externals import joblib


x = []
for i in range(0, 100):
    x.append([1])

X = np.array(x)

Y = np.array([1] * 100)

model = SGDRegressor(loss='squared_loss', alpha=0.1)
model.fit(X, Y)

print model.predict([[1]])
print model.predict([[2]])
print model.predict([[5]])

print model.score(X, Y)

x = []
for i in range(0, 100):
    x.append([2])

X = np.array(x)

Y = np.array([2] * 100)

model.fit(X, Y)
print model.predict([[1]])
print model.predict([[2]])
print model.predict([[5]])

print model.score(np.array([[1], [1]]), np.array([1, 1]))

joblib.dump(model, 'fuck.pk1')

import pdb; pdb.set_trace()
